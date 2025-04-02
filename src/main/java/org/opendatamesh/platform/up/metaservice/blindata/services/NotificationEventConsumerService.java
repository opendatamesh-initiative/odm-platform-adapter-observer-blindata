package org.opendatamesh.platform.up.metaservice.blindata.services;


import org.opendatamesh.platform.up.metaservice.blindata.client.odm.OdmEventNotificationClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationStatus;
import org.opendatamesh.platform.up.metaservice.blindata.services.notificationevents.NotificationEventManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class NotificationEventConsumerService {


    @Autowired
    private OdmEventNotificationClient notificationClient;

    @Lazy
    @Autowired
    private NotificationEventConsumerService self;

    @Autowired
    private NotificationEventManager notificationEventManager;


    public OdmEventNotificationResource consumeEventNotification(OdmEventNotificationResource odmEventNotificationResource) {
        odmEventNotificationResource.setReceivedAt(new Date(System.currentTimeMillis()));
        //TODO add adapter here (DataProductVersionDPDS --> DataProductVersion)
        //TODO odmEventNotificationResource should be copied (another thread uses it)
        self.consumeEventNotificationAsync(odmEventNotificationResource);
        odmEventNotificationResource.setStatus(OdmEventNotificationStatus.PROCESSING);
        return odmEventNotificationResource;
    }

    @Async
    public void consumeEventNotificationAsync(OdmEventNotificationResource odmEventNotificationResource) {
        try {
            odmEventNotificationResource = notificationEventManager.notify(odmEventNotificationResource);
        } finally {
            odmEventNotificationResource.setProcessedAt(new Date(System.currentTimeMillis()));
            //TODO add adapter here (DataProductVersion --> DataProductVersionDPDS)
            updateEventNotificationOnNotificationService(odmEventNotificationResource);
        }
    }

    private void updateEventNotificationOnNotificationService(OdmEventNotificationResource odmEventNotificationResource) {
        notificationClient.updateEventNotification(
                odmEventNotificationResource.getId(), odmEventNotificationResource
        );
    }

}