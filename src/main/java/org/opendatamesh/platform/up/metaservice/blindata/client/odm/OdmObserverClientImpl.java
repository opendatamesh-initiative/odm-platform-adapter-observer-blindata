package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmObserverResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmObserverSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmObserverSubscribeResponseResourceV2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    @Override
    public OdmObserverResource updateObserver(Long id, OdmObserverResource observerResource) {
        return restUtils.put(String.format("%s/%s/observers/{id}", baseUrl, route), null, id, observerResource, OdmObserverResource.class);
    }

    @Override
    public Page<OdmObserverResource> getObservers(Pageable pageable, OdmObserverSearchOptions searchOptions) {
        return restUtils.getPage(String.format("%s/%s/observers", baseUrl, route), null, pageable, searchOptions, OdmObserverResource.class);
    }

    @Override
    public OdmObserverResource getObserver(Long id) {
        return restUtils.get(String.format("%s/%s/observers/{id}", baseUrl, route), null, id, OdmObserverResource.class);
    }

    @Override
    public void removeObserver(Long id) {
        restUtils.delete(String.format("%s/%s/observers/{id}", baseUrl, route), null, id);
    }
}
