package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmObserverResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.OdmObserverSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OdmObserverClient {

    OdmObserverResource addObserver(OdmObserverResource observerResource);

    OdmObserverResource updateObserver(Long id, OdmObserverResource observerResource);

    Page<OdmObserverResource> getObservers(Pageable pageable, OdmObserverSearchOptions searchOptions);

    OdmObserverResource getObserver(Long id);

    void removeObserver(Long id);

}
