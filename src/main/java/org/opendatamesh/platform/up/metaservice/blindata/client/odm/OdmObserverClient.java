package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmObserverResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmObserverSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmObserverSubscribeResponseResourceV2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OdmObserverClient {

    OdmObserverResource addObserver(OdmObserverResource observerResource);

    OdmObserverSubscribeResponseResourceV2 subscribeObserverV2(OdmObserverSubscribeResponseResourceV2.OdmObserverSubscribeResourceV2 observerSubscribeResource);

    OdmObserverResource updateObserver(Long id, OdmObserverResource observerResource);

    Page<OdmObserverResource> getObservers(Pageable pageable, OdmObserverSearchOptions searchOptions);

    OdmObserverResource getObserver(Long id);

    void removeObserver(Long id);

}
