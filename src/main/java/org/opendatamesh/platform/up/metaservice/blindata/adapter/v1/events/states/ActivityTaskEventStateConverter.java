package org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.states;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.EventStatus;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.eventstates.OdmDataProductTaskEventState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.EventType.*;

@Component
public class ActivityTaskEventStateConverter implements EventStateConverter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean accepts(EventType eventType) {
        return Sets.newHashSet(
                DATA_PRODUCT_TASK_CREATED,
                DATA_PRODUCT_TASK_STARTED,
                DATA_PRODUCT_TASK_COMPLETED
        ).contains(eventType);
    }

    @Override
    public Event odmToInternalEvent(OdmEventNotificationResource odmEventNotificationResource) {
        try {
            Event event = new Event();
            event.setEventType(EventType.valueOf(odmEventNotificationResource.getEvent().getType()));
            event.setEventStatus(EventStatus.valueOf(odmEventNotificationResource.getStatus().name()));
            if (odmEventNotificationResource.getEvent().getBeforeState() != null && !odmEventNotificationResource.getEvent().getBeforeState().isNull()) {
                event.setBeforeState(convertState(odmEventNotificationResource.getEvent().getBeforeState()));
            }
            if (odmEventNotificationResource.getEvent().getBeforeState() != null && !odmEventNotificationResource.getEvent().getBeforeState().isNull()) {
                event.setAfterState(convertState(odmEventNotificationResource.getEvent().getAfterState()));
            }
            return event;
        } catch (JacksonException e) {
            log.warn("Impossible to convert odm event to internal event. Event: {} Error: {}", odmEventNotificationResource, e.getMessage());
            return null;
        }
    }

    private ActivityTaskEventState convertState(JsonNode stateNode) throws JacksonException {
        OdmDataProductTaskEventState odmState = objectMapper
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .treeToValue(stateNode, OdmDataProductTaskEventState.class);

        ActivityTaskEventState activityTaskEventState = new ActivityTaskEventState();
        activityTaskEventState.setTask(odmState.getTask());

        return activityTaskEventState;
    }
}
