package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssueCampaignRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.BDDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualitySuiteRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadResultsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;

import java.util.List;
import java.util.Optional;

class QualityUploadBlindataOutboundPortDryRunImpl implements QualityUploadBlindataOutboundPort {

    private final QualityUploadBlindataOutboundPort outboundPort;

    QualityUploadBlindataOutboundPortDryRunImpl(QualityUploadBlindataOutboundPort outboundPort) {
        this.outboundPort = outboundPort;
    }

    @Override
    public BDQualityUploadResultsRes uploadQuality(BDQualitySuiteRes qualitySuite, List<QualityCheck> qualityChecks) {
        return new BDQualityUploadResultsRes();
    }

    @Override
    public Optional<BDIssueCampaignRes> findIssueCampaign(String campaignName) {
        return outboundPort.findIssueCampaign(campaignName);
    }

    @Override
    public BDIssueCampaignRes createIssueCampaign(BDIssueCampaignRes newIssueCampaign) {
        return newIssueCampaign;
    }

    @Override
    public Optional<BDShortUserRes> findDataProductOwner(String username) {
        return outboundPort.findDataProductOwner(username);
    }

    @Override
    public Optional<BDDataProductRes> findDataProduct(String fullyQualifiedName) {
        return outboundPort.findDataProduct(fullyQualifiedName);
    }
}
