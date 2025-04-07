package org.opendatamesh.platform.up.metaservice.blindata.services.notificationevents;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventStatus;
import org.springframework.util.CollectionUtils;

import java.util.List;

public class NotificationEventManager {

    private final List<NotificationEventHandler> eventHandlers;

    public NotificationEventManager(List<NotificationEventHandler> eventHandlers) {
        this.eventHandlers = eventHandlers;
    }

    public Event notify(Event event) {
        if (!CollectionUtils.isEmpty(eventHandlers)) {
            Event updatedEvent = eventHandlers.stream()
                    .filter(eventHandler -> eventHandler.isSubscribedTo(event))
                    .map(eventHandler -> eventHandler.handle(event))
                    .reduce(event, this::concatOutputAndProcessStatus);

            if (!updatedEvent.getEventStatus().equals(EventStatus.PROCESS_ERROR) && !updatedEvent.getEventStatus().equals(EventStatus.UNPROCESSABLE)) {
                updatedEvent.setEventStatus(EventStatus.PROCESSED);
            }
            return updatedEvent;
        }
        return event;
    }

    private Event concatOutputAndProcessStatus(Event acc, Event b) {
        JsonNode accOutput = acc.getProcessingOutput();
        JsonNode bOutput = b.getProcessingOutput();

        ArrayNode mergedOutput = new ObjectMapper().createArrayNode();

        if (accOutput != null) {
            if (accOutput.isArray()) {
                mergedOutput.addAll((ArrayNode) accOutput);
            } else {
                mergedOutput.add(accOutput);
            }
        }
        if (bOutput != null) {
            if (bOutput.isArray()) {
                mergedOutput.addAll((ArrayNode) bOutput);
            } else {
                mergedOutput.add(bOutput);
            }
        }
        acc.setProcessingOutput(mergedOutput);

        if (b.getEventStatus().equals(EventStatus.PROCESS_ERROR) ||
                b.getEventStatus().equals(EventStatus.UNPROCESSABLE)) {
            acc.setEventStatus(b.getEventStatus());
        }

        return acc;
    }
}
