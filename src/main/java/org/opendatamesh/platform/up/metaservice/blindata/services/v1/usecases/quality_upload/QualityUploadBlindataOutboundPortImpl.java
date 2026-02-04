package org.opendatamesh.platform.up.metaservice.blindata.services.v1.usecases.quality_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdIssueCampaignClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdQualityClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdIssueManagementConfig;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssueCampaignRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssuePolicyRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.*;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

class QualityUploadBlindataOutboundPortImpl implements QualityUploadBlindataOutboundPort {

    private final BdQualityClient bdQualityClient;
    private final BdIssueCampaignClient bdIssueClient;
    private final BdUserClient bdUserClient;
    private final BdIssueManagementConfig issuePolicyConfig;
    private final QualityCheckMapper qualityCheckMapper;

    QualityUploadBlindataOutboundPortImpl(BdQualityClient bdQualityClient, BdIssueCampaignClient bdIssueClient, BdUserClient bdUserClient, BdIssueManagementConfig issuePolicyConfig, QualityCheckMapper qualityCheckMapper) {
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
    public Optional<BDShortUserRes> findUser(String username) {
        return bdUserClient.getBlindataUser(username);
    }
}
