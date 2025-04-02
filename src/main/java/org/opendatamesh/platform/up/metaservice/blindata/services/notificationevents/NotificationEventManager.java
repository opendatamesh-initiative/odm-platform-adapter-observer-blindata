package org.opendatamesh.platform.up.metaservice.blindata.services.notificationevents;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationStatus;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class NotificationEventManager {

    private final List<NotificationEventHandler> eventHandlers;

    public NotificationEventManager(List<NotificationEventHandler> eventHandlers) {
        this.eventHandlers = eventHandlers;
    }

    public OdmEventNotificationResource notify(OdmEventNotificationResource event) {
        OdmEventNotificationResource updatedEvent = event;
        if (!CollectionUtils.isEmpty(eventHandlers)) {
            updatedEvent = eventHandlers.stream()
                    .filter(eventHandler -> eventHandler.isSubscribedTo(event))
                    .map(eventHandler -> eventHandler.handle(event))
                    .reduce(event, this::concatOutputAndProcessStatus);
        }
        if (updatedEvent.getStatus().equals(OdmEventNotificationStatus.PROCESSING)) {
            updatedEvent.setStatus(OdmEventNotificationStatus.PROCESSED);
        }
        return updatedEvent;
    }

    private OdmEventNotificationResource concatOutputAndProcessStatus(OdmEventNotificationResource acc, OdmEventNotificationResource b) {
        acc.setProcessingOutput(acc.getProcessingOutput() + "\n\n" + b.getProcessingOutput());
        if (b.getStatus().equals(OdmEventNotificationStatus.PROCESS_ERROR) || b.getStatus().equals(OdmEventNotificationStatus.UNPROCESSABLE)) {
            acc.setStatus(b.getStatus());
        }
        return acc;
    }
}
