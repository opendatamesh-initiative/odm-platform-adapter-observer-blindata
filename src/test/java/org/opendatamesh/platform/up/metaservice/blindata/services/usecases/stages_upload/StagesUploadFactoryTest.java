package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.stages_upload;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.dpds.model.info.Info;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductStagesUploadRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.devops.v2.OdmActivityResourceV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.devops.v2.OdmActivityStatusV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventTypeV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventV2;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.opendatamesh.platform.up.metaservice.blindata.services.v2.devopsevents.DevOpsActivityEventResolver;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * DevOps 2.0 activity outcomes on {@link StagesUploadFactory} (BDMD-5558).
 */
@ExtendWith(MockitoExtension.class)
class StagesUploadFactoryTest {

    @Mock
    private BdDataProductClient bdDataProductClient;
    @Mock
    private DevOpsActivityEventResolver devOpsActivityEventResolver;

    private StagesUploadFactory factory;

    @BeforeEach
    void setUp() {
        factory = new StagesUploadFactory();
        ReflectionTestUtils.setField(factory, "bdDataProductClient", bdDataProductClient);
        ReflectionTestUtils.setField(factory, "objectMapper", new ObjectMapper());
        ReflectionTestUtils.setField(factory, "devOpsActivityEventResolver", devOpsActivityEventResolver);
    }

    /**
     * <pre>
     * Scenario: Stages cannot be configured on a failed activity
     *   Given STAGES_UPLOAD is asked to build a use case
     *   When the event is ACTIVITY_FAILED
     *   Then the factory refuses it as an unsupported event type
     * </pre>
     */
    @Test
    void whenActivityFailedThenUnsupportedEventType() {
        EventV2 event = event(EventTypeV2.ACTIVITY_FAILED);

        assertThatThrownBy(() -> factory.getUseCaseV2(event))
                .isInstanceOf(UseCaseInitException.class)
                .hasMessageContaining("unsupported event type: ACTIVITY_FAILED");
        verifyNoInteractions(devOpsActivityEventResolver);
    }

    @Test
    void whenActivitySucceededThenUseCaseUploadsTheActivityNameAsStageWithDescriptorVersion() {
        stubResolver(OdmActivityStatusV2.SUCCEEDED);
        stubExistingDataProduct();
        EventV2 event = event(EventTypeV2.ACTIVITY_SUCCEEDED);

        factory.getUseCaseV2(event).execute();

        ArgumentCaptor<BDDataProductStagesUploadRes> captor = ArgumentCaptor.forClass(BDDataProductStagesUploadRes.class);
        verify(bdDataProductClient).uploadStages(captor.capture());
        assertThat(captor.getValue().getDataProductUuid()).isEqualTo("bd-uuid-1");
        assertThat(captor.getValue().getStages()).singleElement().satisfies(stage -> {
            assertThat(stage.getName()).isEqualTo("dev");
            assertThat(stage.getVersion()).isEqualTo("1.0.0");
        });
    }

    @Test
    void whenActivityStatusIsNotSucceededThenNoStageIsUploaded() {
        stubResolver(OdmActivityStatusV2.FAILED);
        stubExistingDataProduct();

        factory.getUseCaseV2(event(EventTypeV2.ACTIVITY_SUCCEEDED)).execute();

        verify(bdDataProductClient, never()).uploadStages(any());
    }

    private void stubResolver(OdmActivityStatusV2 status) {
        DataProductVersion descriptor = new DataProductVersion();
        Info info = new Info();
        info.setFullyQualifiedName("urn:org.opendatamesh:dataproducts:tripExecution");
        info.setVersion("1.0.0");
        descriptor.setInfo(info);
        OdmActivityResourceV2 activity = new OdmActivityResourceV2();
        activity.setName("dev");
        activity.setStatus(status);
        when(devOpsActivityEventResolver.dataProductVersionOf(any())).thenReturn(descriptor);
        when(devOpsActivityEventResolver.activityOf(any())).thenReturn(activity);
    }

    private void stubExistingDataProduct() {
        BDDataProductRes product = new BDDataProductRes();
        product.setUuid("bd-uuid-1");
        product.setIdentifier("urn:org.opendatamesh:dataproducts:tripExecution");
        when(bdDataProductClient.getDataProduct("urn:org.opendatamesh:dataproducts:tripExecution")).thenReturn(Optional.of(product));
    }

    private EventV2 event(EventTypeV2 type) {
        EventV2 event = new EventV2();
        event.setEventType(type);
        event.setResourceType("ACTIVITY");
        event.setResourceIdentifier("act-uuid-1");
        event.setEventContent(new ObjectMapper().createObjectNode().put("placeholder", true));
        return event;
    }
}
