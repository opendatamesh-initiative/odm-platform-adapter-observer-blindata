package org.opendatamesh.platform.up.metaservice.blindata.controllers;

import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.NotificationEventConsumerService;
import org.opendatamesh.platform.up.observer.api.controllers.AbstractConsumeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "observer/notifications")
public class ConsumeControllerImpl extends AbstractConsumeController {

    @Autowired
    NotificationEventConsumerService notificationEventConsumerService;

    @Override
    public EventNotificationResource consumeEventNotification(EventNotificationResource notification) {
        return notificationEventConsumerService.consumeEventNotification(notification);
    }
}