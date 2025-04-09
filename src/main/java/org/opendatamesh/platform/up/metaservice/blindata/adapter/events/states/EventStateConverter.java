package org.opendatamesh.platform.up.metaservice.blindata.adapter.events.states;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;
import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.EventType;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;

public interface EventStateConverter {

    boolean accepts(EventType eventType);

    Event odmToInternalEvent(OdmEventNotificationResource odmEventNotificationResource);
}
