package org.opendatamesh.platform.up.metaservice.blindata.services;


import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventAdapter;
import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmEventNotificationClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationStatus;
import org.opendatamesh.platform.up.metaservice.blindata.services.notificationevents.NotificationEventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Optional;

@Service
public class NotificationEventConsumerService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OdmEventNotificationClient notificationClient;
    @Lazy
    @Autowired
    private NotificationEventConsumerService self;
    @Autowired
    private NotificationEventManager notificationEventManager;
    @Autowired
    private EventAdapter eventAdapter;

    public OdmEventNotificationResource consumeEventNotification(OdmEventNotificationResource odmEventNotificationResource) {
        odmEventNotificationResource.setReceivedAt(new Date(System.currentTimeMillis()));
        self.consumeEventNotificationAsync(
                //Deep copy of the notification object so it is thread safe
                new OdmEventNotificationResource(odmEventNotificationResource)
        );
        odmEventNotificationResource.setStatus(OdmEventNotificationStatus.PROCESSING);
        return odmEventNotificationResource;
    }

    @Async
    public void consumeEventNotificationAsync(OdmEventNotificationResource odmEventNotificationResource) {
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