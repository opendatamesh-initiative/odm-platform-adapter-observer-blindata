package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import com.google.common.collect.Lists;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.client.blindata.exceptions.BlindataClientResourceMappingException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtilsFactory;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.exceptions.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.exceptions.ClientResourceMappingException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.HttpHeader;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.Oauth2;
import org.opendatamesh.platform.up.metaservice.blindata.configurations.BdDataProductConfig;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.collaboration.*;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssueCampaignRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.issuemngt.BDIssueCampaignsSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.logical.*;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.marketplace.BDMarketplaceAccessRequestsUploadRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDSystemRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.physical.BDSystemSearchOptions;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.product.*;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindata.quality.BDQualityUploadResultsRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BdClientImpl implements BdDataProductClient, BdStewardshipClient, BdUserClient, BdPolicyEvaluationResultClient, BdSemanticLinkingClient, BdQualityClient, BdIssueCampaignClient, BdGovernancePolicyClient, BdGovernancePolicySuiteClient, BdGovernancePolicyImplementationClient, BdMarketplaceAccessRequestsUploadResultClient, BdSystemClient {

    private final BdCredentials credentials;
    private final BdDataProductConfig dataProductClientConfig;
    private final RestUtils restUtils;
    private final RestUtils asyncRestUtils;

    public BdClientImpl(
            BdCredentials bdCredentials,
            BdDataProductConfig dataProductClientConfig,
            RestTemplate restTemplate
    ) {
        this.credentials = bdCredentials;
        this.dataProductClientConfig = dataProductClientConfig;

        List<HttpHeader> authenticatedHeaders = new ArrayList<>();
        authenticatedHeaders.add(new HttpHeader("X-BD-Tenant", credentials.getBlindataTenantUuid()));
        if (credentials.apiKeyIsConfigured()) {
            authenticatedHeaders.add(new HttpHeader("X-BD-User", credentials.getBlindataUsername()));
            authenticatedHeaders.add(new HttpHeader("X-BD-ApiKey", credentials.getBlindataPass()));
        }
        Oauth2 oauth2 = null;
        if (StringUtils.hasText(credentials.getOauth2Url())) {
            oauth2 = new Oauth2();
            oauth2.setUrl(credentials.getOauth2Url());
            oauth2.setGrantType(credentials.getOauth2GrantType());
            oauth2.setScope(credentials.getOauth2Scope());
            oauth2.setClientId(credentials.getOauth2ClientId());
            oauth2.setClientSecret(credentials.getOauth2ClientSecret());
            oauth2.setClientCertificate(credentials.getOauth2ClientCertificate());
            oauth2.setClientCertificatePrivateKey(credentials.getOauth2ClientCertificatePrivateKey());
        }
        this.restUtils = RestUtilsFactory.getAuthenticatedRestUtils(
                restTemplate,
                authenticatedHeaders,
                oauth2
        );
        this.asyncRestUtils = RestUtilsFactory.getAuthenticatedAsyncRestUtils(
                restTemplate,
                authenticatedHeaders,
                oauth2,
                "/api/v1/settings/async/request",
                "/api/v1/settings/async/poll"
        );
    }

    @Override
    public BDDataProductRes createDataProduct(BDDataProductRes dataProduct) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            return restUtils.create(
                    String.format("%s/api/v1/dataproducts", credentials.getBlindataUrl()),
                    null,
                    dataProduct,
                    BDDataProductRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public BDDataProductRes putDataProduct(BDDataProductRes dataProduct) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            return restUtils.put(
                    String.format("%s/api/v1/dataproducts/{id}", credentials.getBlindataUrl()),
                    null,
                    dataProduct.getUuid(),
                    dataProduct,
                    BDDataProductRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public BDDataProductRes patchDataProduct(BDDataProductRes dataProduct) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            RestUtils rest = credentials.getEnableAsync() ? asyncRestUtils : restUtils;
            return rest.patch(
                    String.format("%s/api/v1/dataproducts/{id}%s",
                            credentials.getBlindataUrl(),
                            dataProductClientConfig.isAssetsCleanup() ? "?assetsCleanup=true" : ""
                    ),
                    null,
                    dataProduct.getUuid(),
                    dataProduct,
                    BDDataProductRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public Page<BDDataProductRes> getDataProducts(Pageable pageable, BDDataProductSearchOptions filters) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            return restUtils.getPage(
                    String.format("%s/api/v1/dataproducts", credentials.getBlindataUrl()),
                    null,
                    pageable,
                    filters,
                    BDDataProductRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<BDDataProductRes> getDataProduct(String identifier) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            BDDataProductSearchOptions filters = new BDDataProductSearchOptions();
            filters.setIdentifier(identifier);
            return restUtils.getPage(
                    String.format("%s/api/v1/dataproducts", credentials.getBlindataUrl()),
                    null,
                    PageRequest.ofSize(1),
                    filters,
                    BDDataProductRes.class
            ).stream().findFirst();
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }


    @Override
    public void deleteDataProduct(String dataProductUuid) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            restUtils.delete(
                    String.format("%s/api/v1/dataproducts/{id}", credentials.getBlindataUrl()),
                    null,
                    dataProductUuid
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public BDProductPortAssetsRes createDataProductAssets(BDProductPortAssetsRes dataProductPortAssets) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            RestUtils rest = credentials.getEnableAsync() ? asyncRestUtils : restUtils;
            return rest.patch(
                    String.format("%s/api/v1/dataproducts/*/port-assets%s",
                            credentials.getBlindataUrl(),
                            dataProductClientConfig.isAssetsCleanup() ? "?assetsCleanup=true" : ""
                    ),
                    null,
                    null,
                    dataProductPortAssets,
                    BDProductPortAssetsRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public void uploadStages(BDDataProductStagesUploadRes stagesUploadRes) {
        try {
            restUtils.genericPost(
                    String.format("%s/api/v1/dataproducts/*/stages/*/upload", credentials.getBlindataUrl()),
                    null,
                    stagesUploadRes,
                    BDDataProductStagesUploadRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<BDStewardshipResponsibilityRes> getActiveResponsibility(String userUuid, String resourceIdentifier) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            BDStewardshipResponsibilitySearchOptions filters = new BDStewardshipResponsibilitySearchOptions();
            filters.setResourceIdentifier(resourceIdentifier);
            filters.setUserUuid(userUuid);
            filters.setRoleUuid(credentials.getRoleUuid());
            filters.setEndDateIsNull(true);

            return restUtils.getPage(
                    String.format("%s/api/v1/stewardship/responsibilities", credentials.getBlindataUrl()),
                    null,
                    PageRequest.ofSize(1),
                    filters,
                    BDStewardshipResponsibilityRes.class
            ).stream().findFirst();
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public BDStewardshipResponsibilityRes createResponsibility(BDStewardshipResponsibilityRes responsibilityRes) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            return restUtils.create(
                    String.format("%s/api/v1/stewardship/responsibilities", credentials.getBlindataUrl()),
                    null,
                    responsibilityRes,
                    BDStewardshipResponsibilityRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public BDStewardshipRoleRes getRole(String roleUuid) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            return restUtils.get(
                    String.format("%s/api/v1/stewardship/roles/{id}", credentials.getBlindataUrl()),
                    null,
                    roleUuid,
                    BDStewardshipRoleRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<BDShortUserRes> getBlindataUser(String username) throws BlindataClientException, BlindataClientResourceMappingException {
        if (!StringUtils.hasText(username)) {
            return Optional.empty();
        }
        try {
            BDUserSearchOptions filters = new BDUserSearchOptions();
            filters.setTenantUuid(credentials.getBlindataTenantUuid());
            filters.setSearch(username);
            return restUtils.getPage(
                    String.format("%s/api/v1/users", credentials.getBlindataUrl()),
                    null,
                    PageRequest.ofSize(1),
                    filters,
                    BDShortUserRes.class
            ).stream().findFirst();
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public BDPolicyResultsUploadResultsRes createPolicyEvaluationRecords(BDPolicyEvaluationRecords evaluationRecords) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            RestUtils rest = credentials.getEnableAsync() ? asyncRestUtils : restUtils;
            return rest.genericPost(
                    String.format("%s/api/v1/governance-policies/policy-evaluations/*/upload", credentials.getBlindataUrl()),
                    null,
                    evaluationRecords,
                    BDPolicyResultsUploadResultsRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }


    @Override
    //This method giving a semantic path and a namespace
    //returns a semantic link object, which contains full Blindata elements (Logical Field/Data Category)
    public BDLogicalFieldSemanticLinkRes getSemanticLinkElements(String pathString, String defaultNamespaceIdentifier) throws BlindataClientException {
        try {
            BDSemanticLinkingResolveFieldOptions options = new BDSemanticLinkingResolveFieldOptions();
            options.setPathString(pathString);
            options.setDefaultNamespaceIdentifier(defaultNamespaceIdentifier);

            return restUtils.genericGet(
                    credentials.getBlindataUrl() + "/api/v1/logical/semanticlinking/*/resolvefield",
                    null,
                    options,
                    BDLogicalFieldSemanticLinkRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<BDDataCategoryRes> getDataCategoryByNameAndNamespaceUuid(String dataCategoryName, String namespaceUuid) {
        try {
            BDDataCategorySearchOptions filters = new BDDataCategorySearchOptions();
            filters.setNamespaceUuid(Lists.newArrayList(namespaceUuid));
            filters.setSearch(dataCategoryName);

            return restUtils.getPage(
                    credentials.getBlindataUrl() + "/api/v1/datacategories",
                    null,
                    PageRequest.ofSize(1),
                    filters,
                    BDDataCategoryRes.class
            ).stream().findFirst();
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<BDLogicalNamespaceRes> getLogicalNamespaceByIdentifier(String identifier) {
        try {
            BDNamespaceSearchOptions filters = new BDNamespaceSearchOptions();
            filters.setIdentifiers(Lists.newArrayList(identifier));
            return restUtils.getPage(
                    credentials.getBlindataUrl() + "/api/v1/logical/namespaces",
                    null,
                    PageRequest.ofSize(1),
                    filters,
                    BDLogicalNamespaceRes.class
            ).stream().findFirst();
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public BDQualityUploadResultsRes uploadQuality(BDQualityUploadRes qualityUpload) {
        try {
            RestUtils rest = credentials.getEnableAsync() ? asyncRestUtils : restUtils;
            return rest.genericPost(
                    String.format("%s/api/v1/data-quality/suites/*/import-objects%s", credentials.getBlindataUrl(), dataProductClientConfig.isAssetsCleanup() ? "?cleanup=true" : ""),
                    null,
                    qualityUpload,
                    BDQualityUploadResultsRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<BDIssueCampaignRes> getIssueCampaign(String campaignName) {
        BDIssueCampaignsSearchOptions searchOptions = new BDIssueCampaignsSearchOptions();
        searchOptions.setName(campaignName);
        try {
            return restUtils.getPage(
                    String.format("%s/api/v1/issue-management/campaigns", credentials.getBlindataUrl()),
                    null,
                    Pageable.ofSize(1),
                    searchOptions,
                    BDIssueCampaignRes.class
            ).stream().findFirst();
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public BDIssueCampaignRes createCampaign(BDIssueCampaignRes newIssueCampaign) {
        try {
            return restUtils.create(
                    String.format("%s/api/v1/issue-management/campaigns", credentials.getBlindataUrl()),
                    null,
                    newIssueCampaign,
                    BDIssueCampaignRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public Page<BDPolicyRes> getPolicies(BDPoliciesSearchOptions filters, Pageable pageable) {
        try {
            return restUtils.getPage(
                    String.format("%s/api/v1/governance-policies", credentials.getBlindataUrl()),
                    null,
                    pageable,
                    filters,
                    BDPolicyRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public BDPolicyRes createPolicy(BDPolicyRes policy) {
        try {
            return restUtils.create(
                    String.format("%s/api/v1/governance-policies", credentials.getBlindataUrl()),
                    null,
                    policy,
                    BDPolicyRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public BDPolicyRes putPolicyRes(BDPolicyRes policy) {
        try {
            return restUtils.put(
                    String.format("%s/api/v1/governance-policies/{id}", credentials.getBlindataUrl()),
                    null,
                    policy.getUuid(),
                    policy,
                    BDPolicyRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public void deletePolicy(String policyUuid) {
        try {
            restUtils.delete(
                    String.format("%s/api/v1/governance-policies/{id}", credentials.getBlindataUrl()),
                    null,
                    policyUuid
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public Page<BDPolicySuiteRes> getPolicySuites(BDPolicySuiteSearchOptions filters, Pageable pageable) {
        try {
            return restUtils.getPage(
                    String.format("%s/api/v1/governance-policy-suites", credentials.getBlindataUrl()),
                    null,
                    pageable,
                    filters,
                    BDPolicySuiteRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public BDPolicySuiteRes createPolicySuite(BDPolicySuiteRes policySuite) {
        try {
            return restUtils.create(
                    String.format("%s/api/v1/governance-policy-suites", credentials.getBlindataUrl()),
                    null,
                    policySuite,
                    BDPolicySuiteRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public BDPolicySuiteRes putPolicySuite(BDPolicySuiteRes policySuite) {
        try {
            return restUtils.put(
                    String.format("%s/api/v1/governance-policy-suites/{id}", credentials.getBlindataUrl()),
                    null,
                    policySuite.getUuid(),
                    policySuite,
                    BDPolicySuiteRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public void deletePolicySuite(String policySuiteUuid) {
        try {
            restUtils.delete(
                    String.format("%s/api/v1/governance-policy-suites/{id}", credentials.getBlindataUrl()),
                    null,
                    policySuiteUuid
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public Page<BDPolicyImplementationRes> getPolicyImplementations(BDPolicyImplementationsSearchOptions filters, Pageable pageable) {
        try {
            return restUtils.getPage(
                    String.format("%s/api/v1/governance-policies/policy-implementations", credentials.getBlindataUrl()),
                    null,
                    pageable,
                    filters,
                    BDPolicyImplementationRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public BDPolicyImplementationRes createPolicyImplementation(BDPolicyImplementationRes policyImplementation) {
        try {
            return restUtils.create(
                    String.format("%s/api/v1/governance-policies/policy-implementations", credentials.getBlindataUrl()),
                    null,
                    policyImplementation,
                    BDPolicyImplementationRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public BDPolicyImplementationRes putPolicyImplementation(BDPolicyImplementationRes policyImplementation) {
        try {
            return restUtils.put(
                    String.format("%s/api/v1/governance-policies/policy-implementations/{id}", credentials.getBlindataUrl()),
                    null,
                    policyImplementation.getUuid(),
                    policyImplementation,
                    BDPolicyImplementationRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public void deletePolicyImplementation(String implementationUuid) {
        try {
            restUtils.delete(
                    String.format("%s/api/v1/governance-policies/policy-implementations/{id}", credentials.getBlindataUrl()),
                    null,
                    implementationUuid
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public BDMarketplaceAccessRequestPortStatusUploadResultsRes uploadAccessRequestPortStatusRes(BDMarketplaceAccessRequestsUploadRes bdMarketplaceAccessRequestsUploadRes) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            return restUtils.genericPatch(
                    String.format("%s/api/v1/marketplace/requests/*/update/ports", credentials.getBlindataUrl()),
                    null,
                    bdMarketplaceAccessRequestsUploadRes,
                    BDMarketplaceAccessRequestPortStatusUploadResultsRes.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    @Override
    public Optional<BDSystemRes> getSystem(BDSystemSearchOptions searchOptions) {
        try {
            return restUtils.getPage(
                    String.format("%s/api/v1/systems", credentials.getBlindataUrl()),
                    null,
                    Pageable.ofSize(1),
                    searchOptions,
                    BDSystemRes.class
            ).stream().findFirst();
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }
}