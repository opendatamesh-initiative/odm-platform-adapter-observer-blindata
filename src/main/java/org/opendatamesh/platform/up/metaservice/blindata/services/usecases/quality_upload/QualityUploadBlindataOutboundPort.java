package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload;

import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssueCampaignRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityCheckRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualitySuiteRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadResultsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.QualityCheckSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;

import java.util.List;
import java.util.Optional;

interface QualityUploadBlindataOutboundPort {

    BDQualityUploadResultsRes uploadQuality(BDQualitySuiteRes qualitySuite, List<QualityCheck> qualityChecks);

    Optional<BDQualitySuiteRes> findQualitySuiteByCode(String suiteCode);

    List<BDQualityCheckRes> findQualityChecks(QualityCheckSearchOptions options);

    Optional<BDIssueCampaignRes> findIssueCampaign(String campaignName);

    BDIssueCampaignRes createIssueCampaign(BDIssueCampaignRes newIssueCampaign);

    Optional<BDShortUserRes> findUser(String username);
}
