package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmObserverResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmObserverSubscribeResponseResourceV2;

class OdmObserverClientImpl implements OdmObserverClient {
    private static final String route = "api/v1/pp/notification";
    private static final String routeV2 = "api/v2/pp/notification";
    private final String baseUrl;
    private final RestUtils restUtils;

    OdmObserverClientImpl(String baseUrl, RestUtils restUtils) {
        this.baseUrl = baseUrl;
        this.restUtils = restUtils;
    }

    @Override
    public OdmObserverResource addObserver(OdmObserverResource observerResource) {
        return restUtils.create(String.format("%s/%s/observers", baseUrl, route), null, observerResource, OdmObserverResource.class);
    }

    @Override
    public OdmObserverSubscribeResponseResourceV2 subscribeObserverV2(OdmObserverSubscribeResponseResourceV2.OdmObserverSubscribeResourceV2 observerSubscribeResource) {
        return restUtils.genericPost(String.format("%s/%s/subscriptions/subscribe", baseUrl, routeV2), null, observerSubscribeResource, OdmObserverSubscribeResponseResourceV2.class);
    }
}
