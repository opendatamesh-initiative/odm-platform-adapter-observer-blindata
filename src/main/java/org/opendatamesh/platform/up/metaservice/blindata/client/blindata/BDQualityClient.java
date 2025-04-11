package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDUploadResultsMessage;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadRes;

public interface BDQualityClient {
    BDUploadResultsMessage uploadQualityChecks(BDQualityUploadRes qualityUpload);
}
