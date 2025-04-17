package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.BDUploadResultsMessage;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssueCampaignRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualitySuiteRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;

import java.util.List;
import java.util.Optional;

interface QualityUploadBlindataOutboundPort {

    BDUploadResultsMessage uploadQuality(BDQualitySuiteRes qualitySuite, List<QualityCheck> qualityChecks);

    Optional<BDIssueCampaignRes> findIssueCampaign(String campaignName);

    BDIssueCampaignRes createIssueCampaign(BDIssueCampaignRes newIssueCampaign);

    Optional<BDShortUserRes> findDataProductOwner(String username);
}
