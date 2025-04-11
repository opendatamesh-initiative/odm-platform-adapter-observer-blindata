package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDUploadResultsMessage;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualitySuiteRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;

import java.util.List;

interface QualityUploadBlindataOutboundPort {

    BDUploadResultsMessage saveQualityDefinitions(BDQualitySuiteRes qualitySuite, List<QualityCheck> qualityChecks);
}
