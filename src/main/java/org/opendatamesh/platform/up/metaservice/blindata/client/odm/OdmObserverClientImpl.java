package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmObserverResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmObserverSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

class OdmObserverClientImpl implements OdmObserverClient {
    private static final String route = "api/v1/pp/notification";
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
