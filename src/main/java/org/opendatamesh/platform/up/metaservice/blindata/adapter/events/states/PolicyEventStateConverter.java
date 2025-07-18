package org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventStatus;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.eventstates.OdmPolicyNotificationEventState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType.*;

@Component
public class PolicyEventStateConverter implements EventStateConverter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean accepts(EventType eventType) {
        return Sets.newHashSet(
                POLICY_CREATED,
                POLICY_UPDATED,
                POLICY_DELETED
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
            if (odmEventNotificationResource.getEvent().getAfterState() != null && !odmEventNotificationResource.getEvent().getAfterState().isNull()) {
                event.setAfterState(convertState(odmEventNotificationResource.getEvent().getAfterState()));
            }
            return event;
        } catch (JacksonException e) {
            log.warn("Impossible to convert odm event to internal event. Event: {} Error: {}", odmEventNotificationResource, e.getMessage());
            return null;
        }
    }

    private PolicyEventState convertState(JsonNode stateNode) throws JacksonException {
        OdmPolicyNotificationEventState odmState = objectMapper
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .treeToValue(stateNode, OdmPolicyNotificationEventState.class);

        PolicyEventState policyEventState = new PolicyEventState();
        policyEventState.setPolicy(odmState.getPolicy());

        return policyEventState;
    }
}

