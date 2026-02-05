package org.opendatamesh.platform.up.metaservice.blindata.services.v2;

import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmEventNotificationClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationResourceV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationStatusV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventResourceV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventStatusV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventTypeV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventV2;
import org.opendatamesh.platform.up.metaservice.blindata.services.v2.notificationevents.NotificationEventManagerV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class NotificationEventConsumerServiceV2 {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final LinkedBlockingQueue<OdmEventNotificationResourceV2> notificationQueue = new LinkedBlockingQueue<>();
    private static final AtomicBoolean isProcessing = new AtomicBoolean(false);

    @Autowired
    private OdmEventNotificationClient notificationClient;
    @Autowired
    private NotificationEventManagerV2 notificationEventManagerV2;
    @Autowired
    @Lazy
    private NotificationEventConsumerServiceV2 self;

    public OdmEventNotificationResourceV2 consumeEventNotification(OdmEventNotificationResourceV2 odmEventNotificationResource) {
        try {
            notificationQueue.put(new OdmEventNotificationResourceV2(odmEventNotificationResource));
            // Only start processing if no other thread is currently processing
            if (isProcessing.compareAndSet(false, true)) {
                self.processNextNotification();
            }
        } catch (InterruptedException e) {
            log.error("Failed to queue notification", e);
            Thread.currentThread().interrupt();
            odmEventNotificationResource.setStatus(OdmEventNotificationStatusV2.FAILED_TO_PROCESS);
            return odmEventNotificationResource;
        }
        odmEventNotificationResource.setStatus(OdmEventNotificationStatusV2.PROCESSING);
        return odmEventNotificationResource;
    }

    @Async
    public void processNextNotification() {
        try {
            while (!notificationQueue.isEmpty()) {
                OdmEventNotificationResourceV2 notification = notificationQueue.poll();
                try {
                    processNotification(notification);
                } catch (Exception e) {
                    log.error("Error processing notification", e);
                }
            }
        } finally {
            isProcessing.set(false);
            // Double-check if new items were added while we were finishing
            if (!notificationQueue.isEmpty() && isProcessing.compareAndSet(false, true)) {
                self.processNextNotification();
            }
        }
    }

    private void processNotification(OdmEventNotificationResourceV2 odmEventNotificationResource) {
        try {
            EventV2 resolvedEvent = notificationEventManagerV2.notify(odmToInternalEvent(odmEventNotificationResource.getEvent()));
            odmEventNotificationResource.setStatus(OdmEventNotificationStatusV2.valueOf(resolvedEvent.getEventStatus().name()));
            if (resolvedEvent.getErrorMessage() != null) {
                odmEventNotificationResource.setErrorMessage(resolvedEvent.getErrorMessage());
            }
        } finally {
            updateEventNotificationOnNotificationService(odmEventNotificationResource);
        }
    }

    private EventV2 odmToInternalEvent(OdmEventResourceV2 odmEventResource) {
        EventV2 event = new EventV2();
        event.setEventType(EventTypeV2.valueOf(odmEventResource.getType()));
        event.setResourceType(odmEventResource.getResourceType());
        event.setResourceIdentifier(odmEventResource.getResourceIdentifier());
        event.setEventTypeVersion(odmEventResource.getEventTypeVersion());
        event.setEventContent(odmEventResource.getEventContent());
        event.setEventStatus(EventStatusV2.PROCESSING);
        return event;
    }

    private void updateEventNotificationOnNotificationService(OdmEventNotificationResourceV2 odmEventNotificationResource) {
        notificationClient.updateEventNotificationV2(
                odmEventNotificationResource.getSequenceId(), odmEventNotificationResource
        );
    }
}