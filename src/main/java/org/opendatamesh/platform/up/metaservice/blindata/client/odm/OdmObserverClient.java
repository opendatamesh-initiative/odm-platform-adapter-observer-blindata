package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v1.OdmObserverResource;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.notification.v2.OdmObserverSubscribeResponseResourceV2;

public interface OdmObserverClient {

    OdmObserverResource addObserver(OdmObserverResource observerResource);

    OdmObserverSubscribeResponseResourceV2 subscribeObserverV2(OdmObserverSubscribeResponseResourceV2.OdmObserverSubscribeResourceV2 observerSubscribeResource);
}
