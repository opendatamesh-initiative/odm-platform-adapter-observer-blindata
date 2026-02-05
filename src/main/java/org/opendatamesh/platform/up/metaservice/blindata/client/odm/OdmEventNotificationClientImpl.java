package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmEventNotificationResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmEventNotificationResourceV2;

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
}
