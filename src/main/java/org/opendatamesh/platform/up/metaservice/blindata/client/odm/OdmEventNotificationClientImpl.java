package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationResourceV2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class OdmEventNotificationClientImpl implements OdmEventNotificationClient {

    private static final String route = "api/v1/pp/notification";
    private static final String routeV2 = "api/v2/pp/notification";
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
    public OdmEventNotificationResourceV2 updateEventNotificationV2(Long sequenceId, OdmEventNotificationResourceV2 eventNotificationResource) {
        return restUtils.put(String.format("%s/%s/notifications/{id}", baseUrl, routeV2), null, sequenceId, eventNotificationResource, OdmEventNotificationResourceV2.class);
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
