package org.opendatamesh.platform.up.metaservice.blindata.services.notificationevents;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.OBEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.OBEventNotificationStatus;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class NotificationEventManager {

    private final List<NotificationEventHandler> eventHandlers;

    public NotificationEventManager(List<NotificationEventHandler> eventHandlers) {
        this.eventHandlers = eventHandlers;
    }

    public OBEventNotificationResource notify(OBEventNotificationResource event) {
        OBEventNotificationResource updatedEvent = event;
        if (!CollectionUtils.isEmpty(eventHandlers)) {
            updatedEvent = eventHandlers.stream()
                    .filter(eventHandler -> eventHandler.isSubscribedTo(event))
                    .map(eventHandler -> eventHandler.handle(event))
                    .reduce(event, this::concatOutputAndProcessStatus);
        }
        if (updatedEvent.getStatus().equals(OBEventNotificationStatus.PROCESSING)) {
            updatedEvent.setStatus(OBEventNotificationStatus.PROCESSED);
        }
        return updatedEvent;
    }

    private OBEventNotificationResource concatOutputAndProcessStatus(OBEventNotificationResource acc, OBEventNotificationResource b) {
        acc.setProcessingOutput(acc.getProcessingOutput() + "\n\n" + b.getProcessingOutput());
        if (b.getStatus().equals(OBEventNotificationStatus.PROCESS_ERROR) || b.getStatus().equals(OBEventNotificationStatus.UNPROCESSABLE)) {
            acc.setStatus(b.getStatus());
        }
        return acc;
    }
}
