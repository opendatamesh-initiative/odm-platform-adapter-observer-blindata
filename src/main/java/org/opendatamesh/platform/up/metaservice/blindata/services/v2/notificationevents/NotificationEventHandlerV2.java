package org.opendatamesh.platform.up.metaservice.blindata.services.v2.notificationevents;

import org.opendatamesh.platform.up.metaservice.blindata.adapter.v2.events.EventV2;

public interface NotificationEventHandlerV2 {

    EventV2 handle(EventV2 event);

    boolean isSubscribedTo(EventV2 event);
}
