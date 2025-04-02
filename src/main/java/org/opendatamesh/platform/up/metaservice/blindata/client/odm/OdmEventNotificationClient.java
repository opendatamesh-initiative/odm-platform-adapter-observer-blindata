package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OdmEventNotificationClient {

    OdmEventNotificationResource updateEventNotification(Long notificationId, OdmEventNotificationResource eventNotificationResource);

    OdmEventNotificationResource readOneEventNotification(Long notificationId);

    Page<OdmEventNotificationResource> searchEventNotifications(Pageable pageable, OdmEventNotificationSearchOptions searchOption);

}
