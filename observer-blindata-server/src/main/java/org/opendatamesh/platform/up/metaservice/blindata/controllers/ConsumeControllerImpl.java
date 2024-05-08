package org.opendatamesh.platform.up.metaservice.blindata.controllers;

import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.services.ConsumeService;
import org.opendatamesh.platform.up.observer.api.controllers.AbstractConsumeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsumeControllerImpl extends AbstractConsumeController {

    @Autowired
    ConsumeService consumeService;

    @Override
    public EventNotificationResource consumeEventNotification(EventNotificationResource notification) {
        return consumeService.consumeEventNotification(notification);
    }

}