package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmEventNotificationSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class OdmEventNotificationClientImpl implements OdmEventNotificationClient {

    private static final String route = "api/v1/pp/notification";
    private final String baseUrl;
    private final RestUtils restUtils;

    OdmEventNotificationClientImpl(String baseUrl, RestUtils restUtils) {
        this.baseUrl = baseUrl;
        this.restUtils = restUtils;
    }


    @Override
    public OdmEventNotificationResource updateEventNotification(Long notificationId, OdmEventNotificationResource eventNotificationResource) {
        return restUtils.put(String.format("%s/%s/notifications/{id}", baseUrl, route), null, notificationId, eventNotificationResource, OdmEventNotificationResource.class);
    }

    @Override
    public OdmEventNotificationResource readOneEventNotification(Long notificationId) {
        return restUtils.get(String.format("%s/%s/notifications/{id}", baseUrl, route), null, notificationId, OdmEventNotificationResource.class);
    }

    @Override
    public Page<OdmEventNotificationResource> searchEventNotifications(Pageable pageable, OdmEventNotificationSearchOptions searchOption) {
        return restUtils.getPage(String.format("%s/%s/notifications", baseUrl, route), null, pageable, searchOption, OdmEventNotificationResource.class);
    }
}
