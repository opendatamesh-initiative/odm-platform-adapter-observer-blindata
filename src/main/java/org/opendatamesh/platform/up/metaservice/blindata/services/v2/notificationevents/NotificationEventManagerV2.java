package org.opendatamesh.platform.up.metaservice.blindata.services.v2.notificationevents;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventStatusV2;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventV2;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class NotificationEventManagerV2 {

    private final List<NotificationEventHandlerV2> eventHandlers;

    public NotificationEventManagerV2(List<NotificationEventHandlerV2> eventHandlers) {
        this.eventHandlers = eventHandlers;
    }

    public EventV2 notify(EventV2 event) {
        if (!CollectionUtils.isEmpty(eventHandlers)) {
            EventV2 updatedEvent = eventHandlers.stream()
                    .filter(eventHandler -> eventHandler.isSubscribedTo(event))
                    .map(eventHandler -> eventHandler.handle(event))
                    .reduce(event, this::concatErrorMessageAndProcessStatus);

            if (!updatedEvent.getEventStatus().equals(EventStatusV2.FAILED_TO_PROCESS)) {
                updatedEvent.setEventStatus(EventStatusV2.PROCESSED);
            }
            return updatedEvent;
        }
        return event;
    }

    private EventV2 concatErrorMessageAndProcessStatus(EventV2 acc, EventV2 b) {
        if (b.getErrorMessage() != null) {
            if (acc.getErrorMessage() != null) {
                acc.setErrorMessage(acc.getErrorMessage() + "; " + b.getErrorMessage());
            } else {
                acc.setErrorMessage(b.getErrorMessage());
            }
        }
        if (b.getEventStatus().equals(EventStatusV2.FAILED_TO_PROCESS)) {
            acc.setEventStatus(b.getEventStatus());
        }
        return acc;
    }
}
