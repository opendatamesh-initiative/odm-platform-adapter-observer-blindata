package org.opendatamesh.platform.up.metaservice.blindata.services.v2.devopsevents;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmRegistryClient;
import org.opendatamesh.platform.up.metaservice.blindata.exceptions.client.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.devops.v2.OdmActivityResourceV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.devops.v2.OdmActivityStatusV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventTypeV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v2.OdmDataProductVersionResourceV2;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Temporary Registry V2 bridge for DevOps 2.0 activity events (BDMD-5558).
 */
@ExtendWith(MockitoExtension.class)
class DevOpsActivityEventResolverTest {

    private static final String UUID = "dpv-uuid-1";

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Mock
    private OdmRegistryClient odmRegistryClient;

    private DevOpsActivityEventResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new DevOpsActivityEventResolver(odmRegistryClient, objectMapper);
    }

    /**
     * <pre>
     * Scenario: A resolved version is reused within the same event
     *   Given the resolver has already read the version for an event
     *   When a second use case asks for the same event's version
     *   Then Registry V2 is not called again
     * </pre>
     */
    @Test
    void whenSameEventResolvedTwiceThenRegistryCalledOnce() throws IOException {
        when(odmRegistryClient.getDataProductVersionV2(UUID)).thenReturn(Optional.of(registryVersion("1.0.0", "v1.0.0-release")));
        EventV2 event = event("{\"activity\":{\"dataProductVersionUuid\":\"" + UUID + "\",\"name\":\"dev\"}}");

        DataProductVersion first = resolver.dataProductVersionOf(event);
        DataProductVersion second = resolver.dataProductVersionOf(event);

        assertThat(second).isSameAs(first);
        verify(odmRegistryClient, times(1)).getDataProductVersionV2(UUID);
    }

    @Test
    void whenTwoEventInstancesThenRegistryCalledTwice() throws IOException {
        when(odmRegistryClient.getDataProductVersionV2(UUID)).thenReturn(Optional.of(registryVersion("1.0.0", "v1.0.0-release")));
        String content = "{\"activity\":{\"dataProductVersionUuid\":\"" + UUID + "\"}}";

        resolver.dataProductVersionOf(event(content));
        resolver.dataProductVersionOf(event(content));

        verify(odmRegistryClient, times(2)).getDataProductVersionV2(UUID);
    }

    @Test
    void whenVersionNotFoundThenUseCaseInitExceptionNamesRegistryAndUuid() throws IOException {
        when(odmRegistryClient.getDataProductVersionV2(UUID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resolver.dataProductVersionOf(event(activityContent())))
                .isInstanceOf(UseCaseInitException.class)
                .hasMessageContaining("not found in ODM Registry V2")
                .hasMessageContaining(UUID);
    }

    @Test
    void whenClientFailsThenUseCaseInitExceptionKeepsCause() throws IOException {
        RuntimeException failure = new IllegalStateException("ODM Registry V2 is not configured");
        when(odmRegistryClient.getDataProductVersionV2(UUID)).thenThrow(failure);

        assertThatThrownBy(() -> resolver.dataProductVersionOf(event(activityContent())))
                .isInstanceOf(UseCaseInitException.class)
                .hasMessageContaining("Failed to read data product version " + UUID + " from ODM Registry V2")
                .hasMessageContaining("not configured")
                .hasCause(failure);
    }

    /**
     * A Registry V2 HTTP error is reported with the status code only: the response body never reaches the message,
     * which is written back to Notification as the notification's {@code errorMessage}.
     */
    @Test
    void whenRegistryAnswersHttpErrorThenMessageHasStatusButNotResponseBody() throws IOException {
        when(odmRegistryClient.getDataProductVersionV2(UUID)).thenThrow(new ClientException(500, "secret body"));

        assertThatThrownBy(() -> resolver.dataProductVersionOf(event(activityContent())))
                .isInstanceOf(UseCaseInitException.class)
                .hasMessage("Failed to read data product version " + UUID + " from ODM Registry V2: HTTP 500.")
                .hasMessageContaining("HTTP 500")
                .satisfies(e -> assertThat(e.getMessage()).doesNotContain("secret body"));
    }

    @Test
    void whenRegistryReturnsNoContentThenUseCaseInitException() throws IOException {
        OdmDataProductVersionResourceV2 version = registryVersion("1.0.0", "v1.0.0-release");
        version.setContent(null);
        when(odmRegistryClient.getDataProductVersionV2(UUID)).thenReturn(Optional.of(version));

        assertThatThrownBy(() -> resolver.dataProductVersionOf(event(activityContent())))
                .isInstanceOf(UseCaseInitException.class)
                .hasMessageContaining("returned no content")
                .hasMessageContaining(UUID);
    }

    @Test
    void whenDescriptorIsMalformedThenUseCaseInitException() throws IOException {
        OdmDataProductVersionResourceV2 version = registryVersion("1.0.0", "v1.0.0-release");
        version.setContent(objectMapper.readTree("{\"info\":\"not-an-object\"}"));
        when(odmRegistryClient.getDataProductVersionV2(UUID)).thenReturn(Optional.of(version));

        assertThatThrownBy(() -> resolver.dataProductVersionOf(event(activityContent())))
                .isInstanceOf(UseCaseInitException.class)
                .hasMessageContaining("Failed to parse descriptor of data product version " + UUID);
    }

    @Test
    void whenEventContentIsNullThenUseCaseInitException() {
        EventV2 event = new EventV2();
        event.setEventType(EventTypeV2.ACTIVITY_SUCCEEDED);

        assertThatThrownBy(() -> resolver.activityOf(event))
                .isInstanceOf(UseCaseInitException.class)
                .hasMessageContaining("Event content is null");
        verify(odmRegistryClient, never()).getDataProductVersionV2(anyString());
    }

    @Test
    void whenActivityIsMissingThenUseCaseInitException() throws IOException {
        EventV2 event = event("{\"somethingElse\":{}}");

        assertThatThrownBy(() -> resolver.dataProductVersionOf(event))
                .isInstanceOf(UseCaseInitException.class)
                .hasMessageContaining("Missing activity.dataProductVersionUuid");
        verify(odmRegistryClient, never()).getDataProductVersionV2(anyString());
    }

    @Test
    void whenDataProductVersionUuidIsMissingThenUseCaseInitException() throws IOException {
        EventV2 event = event("{\"activity\":{\"name\":\"dev\"}}");

        assertThatThrownBy(() -> resolver.dataProductVersionOf(event))
                .isInstanceOf(UseCaseInitException.class)
                .hasMessageContaining("Missing activity.dataProductVersionUuid");
        verify(odmRegistryClient, never()).getDataProductVersionV2(anyString());
    }

    @Test
    void whenContentCannotBeParsedThenUseCaseInitException() throws IOException {
        EventV2 event = event("{\"activity\":\"not-an-object\"}");

        assertThatThrownBy(() -> resolver.activityOf(event))
                .isInstanceOf(UseCaseInitException.class)
                .hasMessageContaining("Failed to parse ACTIVITY_SUCCEEDED event content");
    }

    @Test
    void whenActivityParsedThenDataProductVersionNumberIsRead() throws IOException {
        EventV2 event = event("{\"activity\":{\"uuid\":\"act-1\",\"dataProductVersionUuid\":\"" + UUID + "\","
                + "\"dataProductFqn\":\"urn:x:dp\",\"dataProductVersionNumber\":\"1.0.0\",\"name\":\"prod\","
                + "\"sortOrder\":1,\"status\":\"SUCCEEDED\",\"tasks\":[{\"executionMode\":\"FULL_CONTROL\"}],\"unknown\":1}}");

        OdmActivityResourceV2 activity = resolver.activityOf(event);

        assertThat(activity.getDataProductVersionNumber()).isEqualTo("1.0.0");
        assertThat(activity.getName()).isEqualTo("prod");
        assertThat(activity.getStatus()).isEqualTo(OdmActivityStatusV2.SUCCEEDED);
        assertThat(activity.getDataProductFqn()).isEqualTo("urn:x:dp");
    }

    @Test
    void whenTagDiffersFromVersionNumberThenDescriptorInfoVersionIsTheVersionNumber() throws IOException {
        when(odmRegistryClient.getDataProductVersionV2(UUID)).thenReturn(Optional.of(registryVersion("1.0.0", "v1.0.0-release")));

        DataProductVersion descriptor = resolver.dataProductVersionOf(event(activityContent()));

        assertThat(descriptor.getInfo().getVersion()).isEqualTo("1.0.0").isNotEqualTo("v1.0.0-release");
        assertThat(descriptor.getInfo().getFullyQualifiedName()).isEqualTo("urn:org.opendatamesh:dataproducts:tripExecution");
    }

    @Test
    void whenReadFailsThenTheFailureIsNotCached() throws IOException {
        when(odmRegistryClient.getDataProductVersionV2(UUID))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(registryVersion("1.0.0", "v1.0.0-release")));
        EventV2 event = event(activityContent());

        assertThatThrownBy(() -> resolver.dataProductVersionOf(event)).isInstanceOf(UseCaseInitException.class);
        DataProductVersion descriptor = resolver.dataProductVersionOf(event);

        assertThat(descriptor.getInfo().getVersion()).isEqualTo("1.0.0");
        verify(odmRegistryClient, times(2)).getDataProductVersionV2(UUID);
    }

    private String activityContent() {
        return "{\"activity\":{\"dataProductVersionUuid\":\"" + UUID + "\",\"dataProductFqn\":\"urn:org.opendatamesh:dataproducts:tripExecution\","
                + "\"dataProductVersionNumber\":\"1.0.0\",\"name\":\"dev\",\"status\":\"SUCCEEDED\"}}";
    }

    private EventV2 event(String content) throws IOException {
        EventV2 event = new EventV2();
        event.setEventType(EventTypeV2.ACTIVITY_SUCCEEDED);
        event.setResourceType("ACTIVITY");
        event.setResourceIdentifier("act-uuid-1");
        JsonNode node = content == null ? NullNode.getInstance() : objectMapper.readTree(content);
        event.setEventContent(node);
        return event;
    }

    private OdmDataProductVersionResourceV2 registryVersion(String versionNumber, String tag) throws IOException {
        String json = "{\"uuid\":\"" + UUID + "\",\"tag\":\"" + tag + "\",\"versionNumber\":\"" + versionNumber + "\","
                + "\"content\":{\"info\":{\"id\":\"info-id-1\",\"fullyQualifiedName\":\"urn:org.opendatamesh:dataproducts:tripExecution\",\"version\":\"" + versionNumber + "\"}}}";
        return objectMapper.readValue(json, OdmDataProductVersionResourceV2.class);
    }
}
