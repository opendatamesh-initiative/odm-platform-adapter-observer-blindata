package org.opendatamesh.platform.up.metaservice.blindata.services.usecases.quality_upload;

import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdIssueCampaignClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdQualityClient;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.BdUserClient;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdIssueManagementConfig;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.BDShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssueCampaignRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssuePolicyRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.*;
import org.opendatamesh.platform.up.metaservice.blindata.resources.internal.quality.QualityCheck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

class QualityUploadBlindataOutboundPortImpl implements QualityUploadBlindataOutboundPort {

    private static final int PAGE_SIZE = 100;

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
    public Optional<BDQualitySuiteRes> findQualitySuiteByCode(String suiteCode) {
        if (!StringUtils.hasText(suiteCode)) {
            return Optional.empty();
        }
        QualitySuitesSearchOptions searchOptions = new QualitySuitesSearchOptions();
        searchOptions.setSearch(suiteCode);
        Page<BDQualitySuiteRes> page = bdQualityClient.getQualitySuites(Pageable.ofSize(PAGE_SIZE), searchOptions);
        return page.getContent().stream()
                .filter(suite -> Objects.equals(suiteCode, suite.getCode()))
                .findFirst();
    }

    @Override
    public List<BDQualityCheckRes> findQualityChecks(QualityCheckSearchOptions options) {
        List<BDQualityCheckRes> allChecks = new ArrayList<>();
        int pageNumber = 0;
        Page<BDQualityCheckRes> page;
        do {
            page = bdQualityClient.getQualityChecks(PageRequest.of(pageNumber, PAGE_SIZE), options);
            allChecks.addAll(page.getContent());
            pageNumber++;
        } while (page.hasNext());
        return allChecks;
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
