package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.opendatamesh.platform.up.metaservice.blindata.client.utils.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.ClientResourceMappingException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.*;
import org.opendatamesh.platform.up.metaservice.blindata.resources.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.exceptions.BlindataClientResourceMappingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class BDClientImpl implements BDDataProductClient, BDStewardshipClient, BDUserClient, BDPolicyEvaluationResultClient {
    private final BDCredentials credentials;
    private final RestUtils restUtils;

    public BDClientImpl(BDCredentials bdCredentials, RestTemplate restTemplate) {
        this.credentials = bdCredentials;
        this.restUtils = new RestUtils(restTemplate);
    }

    @Override
    public BDDataProductRes createDataProduct(BDDataProductRes dataProduct) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            return restUtils.create(
                    String.format("%s/api/v1/dataproducts", credentials.getBlindataUrl()),
                    getAuthenticatedHttpHeaders(),
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
                    getAuthenticatedHttpHeaders(),
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
            return restUtils.patch(
                    String.format("%s/api/v1/dataproducts/{id}", credentials.getBlindataUrl()),
                    getAuthenticatedHttpHeaders(),
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
                    getAuthenticatedHttpHeaders(),
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
                    getAuthenticatedHttpHeaders(),
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
    public void deleteDataProduct(String dataProductIdentifier) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            restUtils.delete(
                    String.format("%s/api/v1/dataproducts/{id}", credentials.getBlindataUrl()),
                    getAuthenticatedHttpHeaders(),
                    dataProductIdentifier
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
            return restUtils.patch(
                    String.format("%s/api/v1/dataproducts/*/port-assets", credentials.getBlindataUrl()),
                    getAuthenticatedHttpHeaders(),
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
                    getAuthenticatedHttpHeaders(),
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
                    getAuthenticatedHttpHeaders(),
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
                    getAuthenticatedHttpHeaders(),
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
                    getAuthenticatedHttpHeaders(),
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
        try {
            BDUserSearchOptions filters = new BDUserSearchOptions();
            filters.setTenantUuid(credentials.getBlindataTenantUuid());
            filters.setSearch(username);
            return restUtils.getPage(
                    String.format("%s/api/v1/users", credentials.getBlindataUrl()),
                    getAuthenticatedHttpHeaders(),
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
    public BDUploadResultsMessage createPolicyEvaluationRecords(BDPolicyEvaluationRecords evaluationRecords) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            return restUtils.genericPost(
                    String.format("%s/api/v1/governance-policies/policy-evaluations/*/upload", credentials.getBlindataUrl()),
                    getAuthenticatedHttpHeaders(),
                    evaluationRecords,
                    BDUploadResultsMessage.class
            );
        } catch (ClientException e) {
            throw new BlindataClientException(e.getCode(), e.getResponseBody());
        } catch (ClientResourceMappingException e) {
            throw new BlindataClientResourceMappingException(e.getMessage(), e);
        }
    }

    private HttpHeaders getAuthenticatedHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-BD-Tenant", credentials.getBlindataTenantUuid());
        headers.set("X-BD-User", credentials.getBlindataUsername());
        headers.set("X-BD-ApiKey", credentials.getBlindataPass());
        return headers;
    }
}