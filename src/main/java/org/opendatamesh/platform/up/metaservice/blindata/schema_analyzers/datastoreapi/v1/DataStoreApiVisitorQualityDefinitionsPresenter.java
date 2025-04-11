package org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.datastoreapi.v1;

import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;

import java.util.List;

interface DataStoreApiVisitorQualityDefinitionsPresenter {
    void presentQualityChecks(List<QualityCheck> qualityChecks);
}
