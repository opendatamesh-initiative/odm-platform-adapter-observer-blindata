package org.opendatamesh.platform.up.metaservice.blindata.services;

import org.opendatamesh.platform.pp.notification.api.clients.EventNotificationClient;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.enums.EventNotificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class ConsumeService {

    @Autowired
    private BlindataService blindataService;

    @Autowired
    private EventNotificationClient notificationClient;

    public EventNotificationResource consumeEventNotification(EventNotificationResource eventNotificationResource) {
        // Update reception time of notification on ODM Notification Service
        eventNotificationResource.setReceivedAt(new Date(System.currentTimeMillis()));
        eventNotificationResource = updateEventNotificationOnNotificationService(eventNotificationResource);
        // Handle notification by Event Type and update it on ODM Notification Service after processing
        switch (eventNotificationResource.getEvent().getType()){
            case "DATA_PRODUCT_VERSION_DELETED":
                eventNotificationResource = blindataService.handleDataProductDelete(eventNotificationResource);
                eventNotificationResource.setProcessedAt(new Date(System.currentTimeMillis()));
                eventNotificationResource = updateEventNotificationOnNotificationService(eventNotificationResource);
                break;
            case "DATA_PRODUCT_VERSION_CREATED":
                eventNotificationResource = blindataService.handleDataProductCreated(eventNotificationResource);
                eventNotificationResource.setProcessedAt(new Date(System.currentTimeMillis()));
                eventNotificationResource = updateEventNotificationOnNotificationService(eventNotificationResource);
                break;
            default:
                eventNotificationResource.setStatus(EventNotificationStatus.UNPROCESSABLE);
                eventNotificationResource.setProcessedAt(new Date(System.currentTimeMillis()));
                eventNotificationResource = updateEventNotificationOnNotificationService(eventNotificationResource);
        }
        return eventNotificationResource;
    }

    private EventNotificationResource updateEventNotificationOnNotificationService(
            EventNotificationResource eventNotificationResource
    ) {
        return notificationClient.updateEventNotification(
                eventNotificationResource.getId(), eventNotificationResource
        );
    }

}