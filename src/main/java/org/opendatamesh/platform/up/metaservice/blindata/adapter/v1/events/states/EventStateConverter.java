package org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.states;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.v1.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationResource;

public interface EventStateConverter {

    boolean accepts(EventType eventType);

    Event odmToInternalEvent(OdmEventNotificationResource odmEventNotificationResource);
}
