package org.opendatamesh.platform.up.metaservice.blindata.client.odm.proxies;

import org.opendatamesh.platform.pp.notification.api.clients.EventNotificationClient;
import org.opendatamesh.platform.pp.notification.api.clients.NotificationClientImpl;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class OdmNotificationProxy implements EventNotificationClient {

    private NotificationClientImpl notificationClient;

    public OdmNotificationProxy(NotificationClientImpl notificationClient) {
        this.notificationClient = notificationClient;
    }

    @Override
    public EventNotificationResource updateEventNotification(Long notificationId, EventNotificationResource eventNotificationResource) {
        if (notificationClient != null) {
            return notificationClient.updateEventNotification(notificationId, eventNotificationResource);
        } else return null;
    }

    @Override
    public EventNotificationResource readOneEventNotification(Long notificationId) {
        if (notificationClient != null) {
            return notificationClient.readOneEventNotification(notificationId);
        } else return null;
    }

    @Override
    public Page<EventNotificationResource> searchEventNotifications(Pageable pageable, EventNotificationSearchOptions searchOption) {
        if (notificationClient != null) {
            return notificationClient.searchEventNotifications(pageable, searchOption);
        } else return null;
    }
}
