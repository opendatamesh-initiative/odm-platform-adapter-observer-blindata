package org.opendatamesh.platform.up.metaservice.blindata.services.v2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmEventNotificationClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationResourceV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationStatusV2;
import org.opendatamesh.platform.up.metaservice.blindata.services.v2.notificationevents.NotificationEventManagerV2;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Edge rules of the V2 notification consumer (BDMD-5558, REASONS Op 7.4: a null {@code event} is treated as
 * unrecognised and the log line must not NPE), from
 * {@code spdd/prompt/BDMD-5558-202611041232-[Feat]-service-devops-v2-events-handling.md}.
 * <p>
 * Plain unit test: {@code self} is the instance itself, so processing runs synchronously in the caller thread.
 */
class NotificationEventConsumerServiceV2Test {

    private OdmEventNotificationClient notificationClient;
    private NotificationEventManagerV2 notificationEventManagerV2;
    private NotificationEventConsumerServiceV2 service;

    @BeforeEach
    void setUp() {
        resetStaticQueueState();
        notificationClient = Mockito.mock(OdmEventNotificationClient.class);
        notificationEventManagerV2 = Mockito.mock(NotificationEventManagerV2.class);
        service = new NotificationEventConsumerServiceV2();
        ReflectionTestUtils.setField(service, "notificationClient", notificationClient);
        ReflectionTestUtils.setField(service, "notificationEventManagerV2", notificationEventManagerV2);
        ReflectionTestUtils.setField(service, "self", service);
    }

    @AfterEach
    void tearDown() {
        resetStaticQueueState();
    }

    /**
     * A notification without {@code event} is ignored as unrecognised: no exception, no dispatch to the use cases,
     * and the notification is closed once as PROCESSED on the Notification service.
     */
    @Test
    void whenNotificationHasNoEventThenIgnoredAndClosedAsProcessed() {
        OdmEventNotificationResourceV2 notification = new OdmEventNotificationResourceV2();
        notification.setSequenceId(7L);
        notification.setStatus(OdmEventNotificationStatusV2.PROCESSING);
        notification.setEvent(null);

        OdmEventNotificationResourceV2[] response = new OdmEventNotificationResourceV2[1];
        assertThatCode(() -> response[0] = service.consumeEventNotification(notification)).doesNotThrowAnyException();

        assertThat(response[0].getStatus()).isEqualTo(OdmEventNotificationStatusV2.PROCESSING);
        ArgumentCaptor<OdmEventNotificationResourceV2> captor = ArgumentCaptor.forClass(OdmEventNotificationResourceV2.class);
        verify(notificationClient, times(1)).updateEventNotificationV2(eq(7L), captor.capture());
        verify(notificationClient, times(1)).updateEventNotificationV2(any(), any());
        assertThat(captor.getValue().getStatus()).isEqualTo(OdmEventNotificationStatusV2.PROCESSED);
        assertThat(captor.getValue().getEvent()).isNull();
        verifyNoInteractions(notificationEventManagerV2);
    }

    /**
     * The queue and the processing flag are static: drain them so no state leaks between tests.
     */
    @SuppressWarnings("unchecked")
    private static void resetStaticQueueState() {
        ((Queue<OdmEventNotificationResourceV2>) ReflectionTestUtils.getField(NotificationEventConsumerServiceV2.class, "notificationQueue")).clear();
        ((AtomicBoolean) ReflectionTestUtils.getField(NotificationEventConsumerServiceV2.class, "isProcessing")).set(false);
    }
}
