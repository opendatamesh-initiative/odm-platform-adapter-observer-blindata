package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDIssueCampaignClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDIssueManagementConfig;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDQualityClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BDUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadResultsRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssueCampaignRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssuePolicyRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityCheckRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualitySuiteRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.QualityCheckMapper;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

class QualityUploadBlindataOutboundPortImpl implements QualityUploadBlindataOutboundPort {

    private final BDQualityClient bdQualityClient;
    private final BDIssueCampaignClient bdIssueClient;
    private final BDUserClient bdUserClient;
    private final BDIssueManagementConfig issuePolicyConfig;
    private final QualityCheckMapper qualityCheckMapper;

    QualityUploadBlindataOutboundPortImpl(BDQualityClient bdQualityClient, BDIssueCampaignClient bdIssueClient, BDUserClient bdUserClient, BDIssueManagementConfig issuePolicyConfig, QualityCheckMapper qualityCheckMapper) {
        this.bdQualityClient = bdQualityClient;
        this.bdIssueClient = bdIssueClient;
        this.bdUserClient = bdUserClient;
        this.issuePolicyConfig = issuePolicyConfig;
        this.qualityCheckMapper = qualityCheckMapper;
    }

    @Override
    public BDQualityUploadResultsRes uploadQuality(BDQualitySuiteRes qualitySuite, List<QualityCheck> qualityChecks) {
        List<BDQualityCheckRes> bdQualityChecks = qualityChecks.stream().map(qualityCheckMapper::toBlindataRes).collect(Collectors.toList());
        Map<String, List<BDIssuePolicyRes>> issuePolicies = new HashMap<>();
        qualityChecks.forEach(qualityCheck -> {
            if (!CollectionUtils.isEmpty(qualityCheck.getIssuePolicies())) {
                issuePolicies.put(qualityCheck.getCode(), qualityCheck.getIssuePolicies());
            }
        });
        if (!issuePolicyConfig.isIssuePoliciesActive()) {
            issuePolicies.values().stream().flatMap(Collection::stream).forEach(
                    issuePolicy -> issuePolicy.setActive(false)
            );
        }
        return bdQualityClient.uploadQuality(
                new BDQualityUploadRes(qualitySuite, bdQualityChecks, issuePolicies)
        );
    }

    @Override
    public Optional<BDIssueCampaignRes> findIssueCampaign(String campaignName) {
        return bdIssueClient.getIssueCampaign(campaignName);
    }

    @Override
    public BDIssueCampaignRes createIssueCampaign(BDIssueCampaignRes newIssueCampaign) {
        return bdIssueClient.createCampaign(newIssueCampaign);
    }

    @Override
    public Optional<BDShortUserRes> findDataProductOwner(String username) {
        return bdUserClient.getBlindataUser(username);
    }
}
