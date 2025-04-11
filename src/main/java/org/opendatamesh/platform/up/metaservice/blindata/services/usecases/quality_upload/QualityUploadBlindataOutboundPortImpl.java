package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDQualityClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDUploadResultsMessage;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityCheckRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualitySuiteRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.QualityCheckMapper;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;

import java.util.List;
import java.util.stream.Collectors;

class QualityUploadBlindataOutboundPortImpl implements QualityUploadBlindataOutboundPort {

    private final BDQualityClient bdQualityClient;
    private final QualityCheckMapper qualityCheckMapper;

    QualityUploadBlindataOutboundPortImpl(BDQualityClient bdQualityClient, QualityCheckMapper qualityCheckMapper) {
        this.bdQualityClient = bdQualityClient;
        this.qualityCheckMapper = qualityCheckMapper;
    }

    @Override
    public BDUploadResultsMessage saveQualityDefinitions(BDQualitySuiteRes qualitySuite, List<QualityCheck> qualityChecks) {
        List<BDQualityCheckRes> bdQualityChecks = qualityChecks.stream().map(qualityCheckMapper::toBlindataRes).collect(Collectors.toList());
        return bdQualityClient.uploadQualityChecks(new BDQualityUploadRes(qualitySuite, bdQualityChecks));
    }
}
