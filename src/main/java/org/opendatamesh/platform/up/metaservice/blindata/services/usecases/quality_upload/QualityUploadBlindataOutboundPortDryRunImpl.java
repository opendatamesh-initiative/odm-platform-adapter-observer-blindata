package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDUploadResultsMessage;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualitySuiteRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;

import java.util.List;

class QualityUploadBlindataOutboundPortDryRunImpl implements QualityUploadBlindataOutboundPort {

    QualityUploadBlindataOutboundPortDryRunImpl() {
    }

    @Override
    public BDUploadResultsMessage saveQualityDefinitions(BDQualitySuiteRes qualitySuite, List<QualityCheck> qualityChecks) {
        return new BDUploadResultsMessage();
    }
}
