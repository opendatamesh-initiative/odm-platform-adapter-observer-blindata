package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.dataproduct_upload;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opendatamesh.dpds.model.DataProductVersion;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.ActivityEventState;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.DataProductEventState;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.DataProductVersionEventState;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.EventState;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdDataProductClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdStewardshipClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdDataProductConfig;
import org.opendatamesh.platform.up.metaservice.blindata.services.usecases.exceptions.UseCaseInitException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class DataProductUploadFactoryTest {

    @Mock
    private BdUserClient bdUserClient;
    @Mock
    private BdDataProductClient bdDataProductClient;
    @Mock
    private BdStewardshipClient bdStewardshipClient;
    @Mock
    private BdDataProductConfig dataProductConfig;

    @InjectMocks
    private DataProductUploadFactory factory;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(factory, "roleUuid", "test-role-uuid");
    }

    @Test
    void getUseCaseDryRun_throwsWhenAfterStateIsNull() {
        assertExplicitInitFailure(
                event(EventType.DATA_PRODUCT_VERSION_CREATED, null),
                "AfterState is null"
        );
    }

    @Test
    void getUseCaseDryRun_throwsWhenDataProductIsNull() {
        assertExplicitInitFailure(
                event(EventType.DATA_PRODUCT_CREATED, new DataProductEventState(null)),
                "dataProduct is null"
        );
    }

    @Test
    void getUseCaseDryRun_throwsWhenDataProductVersionIsNull() {
        DataProductVersionEventState state = new DataProductVersionEventState();
        state.setDataProductVersion(null);

        assertExplicitInitFailure(
                event(EventType.DATA_PRODUCT_VERSION_CREATED, state),
                "dataProductVersion is null"
        );
    }

    @Test
    void getUseCaseDryRun_throwsWhenDataProductVersionInfoIsNull() {
        DataProductVersion dataProductVersion = new DataProductVersion();
        dataProductVersion.setInfo(null);
        DataProductVersionEventState state = new DataProductVersionEventState();
        state.setDataProductVersion(dataProductVersion);

        assertExplicitInitFailure(
                event(EventType.DATA_PRODUCT_VERSION_CREATED, state),
                "dataProductVersion.info is null"
        );
    }

    @Test
    void getUseCaseDryRun_throwsWhenActivityDataProductVersionIsNull() {
        assertExplicitInitFailure(
                event(EventType.DATA_PRODUCT_ACTIVITY_COMPLETED, new ActivityEventState(null, null)),
                "dataProductVersion is null"
        );
    }

    @Test
    void getUseCaseDryRun_throwsWhenActivityDataProductVersionInfoIsNull() {
        DataProductVersion dataProductVersion = new DataProductVersion();
        dataProductVersion.setInfo(null);

        assertExplicitInitFailure(
                event(EventType.DATA_PRODUCT_ACTIVITY_COMPLETED, new ActivityEventState(null, dataProductVersion)),
                "dataProductVersion.info is null"
        );
    }

    private void assertExplicitInitFailure(Event event, String expectedMessageFragment) {
        assertThatThrownBy(() -> factory.getUseCaseDryRun(event))
                .isInstanceOf(UseCaseInitException.class)
                .hasMessageContaining(expectedMessageFragment)
                .satisfies(ex -> assertThat(rootCause(ex))
                        .isInstanceOf(UseCaseInitException.class)
                        .isNotInstanceOf(NullPointerException.class)
                        .hasMessageContaining(expectedMessageFragment));
    }

    private static Event event(EventType eventType, EventState afterState) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setAfterState(afterState);
        return event;
    }

    private static Throwable rootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }
}
