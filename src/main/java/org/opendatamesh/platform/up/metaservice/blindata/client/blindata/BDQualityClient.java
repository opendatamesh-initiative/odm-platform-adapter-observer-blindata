package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadResultsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadRes;

public interface BDQualityClient {
    BDQualityUploadResultsRes uploadQuality(BDQualityUploadRes qualityUpload);
}
