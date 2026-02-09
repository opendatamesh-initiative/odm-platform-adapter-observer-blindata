package org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.DataProductConverter;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventStatus;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType.DATA_PRODUCT_VERSION_CREATED;
import static org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType.DATA_PRODUCT_VERSION_DELETED;

@Component
public class DataProductVersionEventStateConverter implements EventStateConverter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean accepts(EventType eventType) {
        return Sets.newHashSet(
                DATA_PRODUCT_VERSION_CREATED,
                DATA_PRODUCT_VERSION_DELETED
        ).contains(eventType);
    }

    @Override
    public Event odmToInternalEvent(OdmEventNotificationResource odmEventNotificationResource) {
        try {
            Event event = new Event();
            event.setEventType(EventType.valueOf(odmEventNotificationResource.getEvent().getType()));

            if (odmEventNotificationResource.getStatus() == null) {
                odmEventNotificationResource.setStatus(OdmEventNotificationStatus.PROCESSING);
            }
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

    private DataProductVersionEventState convertState(JsonNode stateNode) throws JacksonException {
        DataProductVersionEventState event = new DataProductVersionEventState();
        if (stateNode.has("dataProductVersion")) {
            event.setDataProductVersion(DataProductConverter.oldToNewVersion(stateNode.get("dataProductVersion").toString()));
        }
        return event;

    }
}
