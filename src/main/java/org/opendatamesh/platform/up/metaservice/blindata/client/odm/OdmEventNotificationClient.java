package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationResourceV2;

public interface OdmEventNotificationClient {

    OdmEventNotificationResource updateEventNotification(Long notificationId, OdmEventNotificationResource eventNotificationResource);

    OdmEventNotificationResourceV2 updateEventNotificationV2(Long sequenceId, OdmEventNotificationResourceV2 eventNotificationResource);
}
