package org.opendatamesh.platform.up.metaservice.blindata.adapter.events;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states.EventStateConverter;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EventAdapter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Lazy
    @Autowired
    private List<EventStateConverter> eventStateConverters;

    /**
     * Converts an ODM {@link OdmEventNotificationResource} into an {@link Event}.
     * During conversion, the rawContent fields are removed from the descriptor component,
     * and the descriptor is restored to its original json structure.
     */
    public Optional<Event> odmToInternalEvent(OdmEventNotificationResource eventNotificationResource) {
        if (eventNotificationResource == null || eventNotificationResource.getEvent() == null) {
            log.warn("The event notification is empty or it does not contain a event.");
            return Optional.empty();
        }

        String type = eventNotificationResource.getEvent().getType();
        EventType eventType;
        try {
            eventType = EventType.valueOf(type);
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("The event type {} is not supported by the Blindata Observer.", eventNotificationResource.getEvent().getType());
            return Optional.empty();
        }

        return eventStateConverters.stream()
                .filter(converter -> converter.accepts(eventType))
                .findFirst()
                .map(converter -> converter.odmToInternalEvent(eventNotificationResource));
    }

}
