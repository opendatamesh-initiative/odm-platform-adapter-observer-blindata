package org.opendatamesh.platform.up.metaservice.blindata.services.notificationevents;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.OBEventNotificationResource;

public interface NotificationEventHandler {

    OBEventNotificationResource handle(OBEventNotificationResource event);

    boolean isSubscribedTo(OBEventNotificationResource event);
}
