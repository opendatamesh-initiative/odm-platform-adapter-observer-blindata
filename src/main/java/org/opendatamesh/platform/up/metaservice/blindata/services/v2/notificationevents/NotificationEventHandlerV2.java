package org.opendatamesh.platform.up.metaservice.blindata.services.v2.notificationevents;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.internal.EventV2;

public interface NotificationEventHandlerV2 {

    EventV2 handle(EventV2 event);

    boolean isSubscribedTo(EventV2 event);
}
