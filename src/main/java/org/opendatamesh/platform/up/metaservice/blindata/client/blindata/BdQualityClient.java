package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityCheckRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualitySuiteRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadResultsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.QualityCheckSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.QualitySuitesSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BdQualityClient {
    BDQualityUploadResultsRes uploadQuality(BDQualityUploadRes qualityUpload);

    Page<BDQualitySuiteRes> getQualitySuites(Pageable pageable, QualitySuitesSearchOptions filters);

    Page<BDQualityCheckRes> getQualityChecks(Pageable pageable, QualityCheckSearchOptions filters);
}
