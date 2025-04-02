package org.opendatamesh.platform.up.metaservice.blindata.services.notificationevents;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;

public interface NotificationEventHandler {

    OdmEventNotificationResource handle(OdmEventNotificationResource event);

    boolean isSubscribedTo(OdmEventNotificationResource event);
}
