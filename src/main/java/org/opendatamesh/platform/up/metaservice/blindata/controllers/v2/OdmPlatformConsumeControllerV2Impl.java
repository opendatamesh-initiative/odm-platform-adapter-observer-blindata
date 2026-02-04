package org.opendatamesh.platform.up.metaservice.blindata.controllers.v2;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationResourceV2;
import org.opendatamesh.platform.up.metaservice.blindata.services.v2.NotificationEventConsumerServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v2/up/observer/notifications")
@Tag(
        name = "Consume Notification",
        description = "Endpoints associated to consuming Event's Notifications"
)
public class OdmPlatformConsumeControllerV2Impl {

    @Autowired
    NotificationEventConsumerServiceV2 notificationEventConsumerService;

    @PostMapping
    public OdmEventNotificationResourceV2 consumeEventNotificationV2(
            @RequestBody
            OdmEventNotificationResourceV2 notification
    ) {
        return notificationEventConsumerService.consumeEventNotification(notification);
    }
}