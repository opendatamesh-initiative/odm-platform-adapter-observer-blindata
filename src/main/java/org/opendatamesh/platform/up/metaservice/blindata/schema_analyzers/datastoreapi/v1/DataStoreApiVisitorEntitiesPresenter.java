package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;

interface DataStoreApiVisitorEntitiesPresenter {
    void presentPhysicalEntity(BDPhysicalEntityRes physicalEntity);
}
