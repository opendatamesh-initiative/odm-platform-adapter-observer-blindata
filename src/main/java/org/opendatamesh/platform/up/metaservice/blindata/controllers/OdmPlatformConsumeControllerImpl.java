package org.opendatamesh.platform.up.metaservice.blindata.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.NotificationEventConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/up/observer/notifications")
@Tag(
        name = "Consume Notification",
        description = "Endpoints associated to consuming Event's Notifications"
)
public class OdmPlatformConsumeControllerImpl {

    @Autowired
    NotificationEventConsumerService notificationEventConsumerService;

    @PostMapping
    public OdmEventNotificationResource consumeEventNotification(
            @RequestBody
            OdmEventNotificationResource notification
    ) {
        return notificationEventConsumerService.consumeEventNotification(notification);
    }
}