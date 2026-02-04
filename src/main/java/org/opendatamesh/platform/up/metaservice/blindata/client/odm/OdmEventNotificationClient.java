package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationResourceV2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OdmEventNotificationClient {

    OdmEventNotificationResource updateEventNotification(Long notificationId, OdmEventNotificationResource eventNotificationResource);

    OdmEventNotificationResourceV2 updateEventNotificationV2(Long sequenceId, OdmEventNotificationResourceV2 eventNotificationResource);

    OdmEventNotificationResource readOneEventNotification(Long notificationId);

    Page<OdmEventNotificationResource> searchEventNotifications(Pageable pageable, OdmEventNotificationSearchOptions searchOption);

}
