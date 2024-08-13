package org.opendatamesh.platform.up.metaservice.blindata.services;

import org.opendatamesh.platform.pp.notification.api.clients.EventNotificationClient;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.enums.EventNotificationStatus;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.EventNotificationMapper;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.OBEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.notificationevents.NotificationEventManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class NotificationEventConsumerService {

    @Autowired
    private EventNotificationMapper mapper;

    @Autowired
    private EventNotificationClient notificationClient;

    @Lazy
    @Autowired
    private NotificationEventConsumerService self;

    @Autowired
    private NotificationEventManager notificationEventManager;


    public EventNotificationResource consumeEventNotification(EventNotificationResource eventNotificationResource) {
        eventNotificationResource.setReceivedAt(new Date(System.currentTimeMillis()));
        OBEventNotificationResource obEventNotificationResource = mapper.toObserverResource(eventNotificationResource);
        self.consumeEventNotificationAsync(obEventNotificationResource);
        eventNotificationResource.setStatus(EventNotificationStatus.PROCESSING);
        return eventNotificationResource;
    }

    @Async
    public void consumeEventNotificationAsync(OBEventNotificationResource eventNotificationResource) {
        try {
            eventNotificationResource = notificationEventManager.notify(eventNotificationResource);
        } finally {
            eventNotificationResource.setProcessedAt(new Date(System.currentTimeMillis()));
            updateEventNotificationOnNotificationService(mapper.toPlatformResource(eventNotificationResource));
        }
    }

    private void updateEventNotificationOnNotificationService(
            EventNotificationResource eventNotificationResource
    ) {
        notificationClient.updateEventNotification(
                eventNotificationResource.getId(), eventNotificationResource
        );
    }

}