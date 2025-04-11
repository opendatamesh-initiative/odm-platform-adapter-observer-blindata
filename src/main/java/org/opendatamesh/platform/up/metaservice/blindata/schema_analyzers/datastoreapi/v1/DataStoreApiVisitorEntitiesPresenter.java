package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDPhysicalEntityRes;

import java.util.List;

interface DataStoreApiVisitorEntitiesPresenter {
    void presentPhysicalEntities(List<BDPhysicalEntityRes> physicalEntities);
}
