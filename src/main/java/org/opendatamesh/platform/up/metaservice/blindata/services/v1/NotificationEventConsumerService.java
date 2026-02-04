package org.opendatamesh.platform.up.metaservice.blindata.services.v1;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.EventAdapter;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmEventNotificationClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationStatus;
import org.opendatamesh.platform.up.metaservice.blindata.services.v1.notificationevents.NotificationEventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class NotificationEventConsumerService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final LinkedBlockingQueue<OdmEventNotificationResource> notificationQueue = new LinkedBlockingQueue<>();
    private static final AtomicBoolean isProcessing = new AtomicBoolean(false);

    @Autowired
    private OdmEventNotificationClient notificationClient;
    @Autowired
    private NotificationEventManager notificationEventManager;
    @Autowired
    private EventAdapter eventAdapter;
    @Autowired
    @Lazy
    private NotificationEventConsumerService self;

    public OdmEventNotificationResource consumeEventNotification(OdmEventNotificationResource odmEventNotificationResource) {
        odmEventNotificationResource.setReceivedAt(new Date(System.currentTimeMillis()));
        try {
            notificationQueue.put(new OdmEventNotificationResource(odmEventNotificationResource));
            // Only start processing if no other thread is currently processing
            if (isProcessing.compareAndSet(false, true)) {
                self.processNextNotification();
            }
        } catch (InterruptedException e) {
            log.error("Failed to queue notification", e);
            Thread.currentThread().interrupt();
            odmEventNotificationResource.setStatus(OdmEventNotificationStatus.PROCESS_ERROR);
            return odmEventNotificationResource;
        }
        odmEventNotificationResource.setStatus(OdmEventNotificationStatus.PROCESSING);
        return odmEventNotificationResource;
    }

    @Async
    public void processNextNotification() {
        try {
            while (!notificationQueue.isEmpty()) {
                OdmEventNotificationResource notification = notificationQueue.poll();
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

    private void processNotification(OdmEventNotificationResource odmEventNotificationResource) {
        try {
            Optional<Event> event = eventAdapter.odmToInternalEvent(odmEventNotificationResource);
            if (event.isPresent()) {
                Event resolvedEvent = notificationEventManager.notify(event.get());
                odmEventNotificationResource.setStatus(OdmEventNotificationStatus.valueOf(resolvedEvent.getEventStatus().name()));
                if (resolvedEvent.getProcessingOutput() != null) {
                    odmEventNotificationResource.setProcessingOutput(resolvedEvent.getProcessingOutput().toString());
                }
            } else {
                log.warn("Unsupported event notification: {}", odmEventNotificationResource);
                odmEventNotificationResource.setStatus(OdmEventNotificationStatus.UNPROCESSABLE);
            }
        } finally {
            odmEventNotificationResource.setProcessedAt(new Date(System.currentTimeMillis()));
            updateEventNotificationOnNotificationService(odmEventNotificationResource);
        }
    }

    private void updateEventNotificationOnNotificationService(OdmEventNotificationResource odmEventNotificationResource) {
        notificationClient.updateEventNotification(
                odmEventNotificationResource.getId(), odmEventNotificationResource
        );
    }
}