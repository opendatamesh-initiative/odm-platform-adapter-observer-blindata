package org.opendatamesh.platform.up.metaservice.blindata.services.v1.notificationevents;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.events.Event;

public interface NotificationEventHandler {

    Event handle(Event event);

    boolean isSubscribedTo(Event event);
}
