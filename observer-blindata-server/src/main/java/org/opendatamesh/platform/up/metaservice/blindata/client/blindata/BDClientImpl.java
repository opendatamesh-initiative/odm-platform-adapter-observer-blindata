package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.AuthenticatedRestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.ClientResourceMappingException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.resources.blindataresources.*;
import org.opendatamesh.platform.up.metaservice.blindata.resources.exceptions.BlindataClientException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.exceptions.BlindataClientResourceMappingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BDClientImpl implements BDDataProductClient, BDStewardshipClient, BDUserClient, BDPolicyEvaluationResultClient, BDSemanticLinkingClient {
    /**
     * Cache for storing OAuth2 access tokens. Tokens expire after 1 hour.
     */
    private static final Cache<String, String> tokenCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();
    /**
     * Cache for storing the expiry times of OAuth2 access tokens.
     */
    private static final Cache<String, Instant> tokenExpiryCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    private final BDCredentials credentials;
    private final BDDataProductClientConfig dataProductClientConfig;
    private final RestUtils authenticatedRestUtils;

    public BDClientImpl(
            BDCredentials bdCredentials,
            BDDataProductClientConfig dataProductClientConfig,
            RestTemplate restTemplate
    ) {
        this.credentials = bdCredentials;
        this.dataProductClientConfig = dataProductClientConfig;
        this.authenticatedRestUtils = new AuthenticatedRestUtils(restTemplate, this::getAuthenticatedHttpHeaders);
    }

    @Override
    public BDDataProductRes createDataProduct(BDDataProductRes dataProduct) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            return authenticatedRestUtils.create(
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
            return authenticatedRestUtils.put(
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
            return authenticatedRestUtils.patch(
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
            return authenticatedRestUtils.getPage(
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
            return authenticatedRestUtils.getPage(
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
    public void deleteDataProduct(String dataProductIdentifier) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            authenticatedRestUtils.delete(
                    String.format("%s/api/v1/dataproducts/{id}", credentials.getBlindataUrl()),
                    null,
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
            return authenticatedRestUtils.patch(
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
            authenticatedRestUtils.genericPost(
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

            return authenticatedRestUtils.getPage(
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
            return authenticatedRestUtils.create(
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
            return authenticatedRestUtils.get(
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
            return authenticatedRestUtils.getPage(
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
    public BDUploadResultsMessage createPolicyEvaluationRecords(BDPolicyEvaluationRecords evaluationRecords) throws BlindataClientException, BlindataClientResourceMappingException {
        try {
            return authenticatedRestUtils.genericPost(
                    String.format("%s/api/v1/governance-policies/policy-evaluations/*/upload", credentials.getBlindataUrl()),
                    null,
                    evaluationRecords,
                    BDUploadResultsMessage.class
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
    public LogicalFieldSemanticLinkRes getSemanticLinkElements(String pathString, String defaultNamespaceIdentifier) throws BlindataClientException {
        try {
            BDSemanticLinkingResolveFieldOptions options = new BDSemanticLinkingResolveFieldOptions();
            options.setPathString(pathString);
            options.setDefaultNamespaceIdentifier(defaultNamespaceIdentifier);

            return authenticatedRestUtils.genericGet(
                    credentials.getBlindataUrl() + "/api/v1/logical/semanticlinking/*/resolvefield",
                    null,
                    options,
                    LogicalFieldSemanticLinkRes.class
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

            return authenticatedRestUtils.getPage(
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
            return authenticatedRestUtils.getPage(
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

    private HttpHeaders getAuthenticatedHttpHeaders(HttpHeaders headers) {
        HttpHeaders authenticatedHeaders = headers != null ? new HttpHeaders(headers) : new HttpHeaders();
        authenticatedHeaders.set("X-BD-Tenant", credentials.getBlindataTenantUuid());

        if (credentials.apiKeyIsConfigured()) {
            authenticatedHeaders.set("X-BD-User", credentials.getBlindataUsername());
            authenticatedHeaders.set("X-BD-ApiKey", credentials.getBlindataPass());
            return authenticatedHeaders;
        }
        if (credentials.oauth2IsConfigured()) {
            String accessToken = getCachedOauth2Token().orElseGet(this::retrieveOauth2Token);
            if (accessToken != null) {
                authenticatedHeaders.setBearerAuth(accessToken);
                return authenticatedHeaders;
            } else {
                throw new IllegalStateException("Failed to retrieve OAuth2 token.");
            }
        }
        throw new IllegalStateException("Failed to authenticate headers");
    }

    private synchronized Optional<String> getCachedOauth2Token() {
        Instant expiryTime = tokenExpiryCache.getIfPresent(generateOauth2CacheKey());
        if (expiryTime != null && Instant.now().isBefore(expiryTime)) {
            return Optional.ofNullable(tokenCache.getIfPresent(generateOauth2CacheKey()));
        }
        return Optional.empty();
    }

    private String retrieveOauth2Token() {
        MultiValueMap<String, String> requestBody = StringUtils.hasText(credentials.getOauth2ClientSecret()) ?
                buildRequestWithSharedSecret() :
                buildRequestWithCertificate();

        HttpHeaders tokenRequestHeaders = new HttpHeaders();
        tokenRequestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        try {
            ResponseEntity<ObjectNode> response = new RestTemplate().exchange(
                    credentials.getOauth2Url(),
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody, tokenRequestHeaders),
                    ObjectNode.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String accessToken = Optional.of(response.getBody().get("access_token"))
                        .orElseThrow(() -> new IllegalStateException("Failed to retrieve token: empty token"))
                        .asText();
                int expiresIn = response.getBody().has("expires_in") ? response.getBody().get("expires_in").asInt(3600) : 3600; // Default to 1 hour if missing
                Instant expiryTime = Instant.now().plusSeconds(expiresIn);
                String cacheKey = generateOauth2CacheKey();
                tokenCache.put(cacheKey, accessToken);
                tokenExpiryCache.put(cacheKey, expiryTime);
                return accessToken;
            } else {
                throw new IllegalStateException("Failed to retrieve token: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error while retrieving OAuth2 token: " + e.getMessage(), e);
        }
    }

    private MultiValueMap<String, String> buildRequestWithCertificate() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", credentials.getOauth2GrantType());
        requestBody.add("client_id", credentials.getOauth2ClientId());
        requestBody.add("scope", credentials.getOauth2Scope());
        requestBody.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        try {
            String clientAssertion = generateClientAssertionWithCertificate();
            requestBody.add("client_assertion", clientAssertion);
        } catch (Exception e) {
            throw new InternalServerException(e);
        }
        return requestBody;
    }

    private String generateClientAssertionWithCertificate() throws Exception {
        String privateKey = credentials.getOauth2ClientCertificatePrivateKey();
        String certificate = credentials.getOauth2ClientCertificate();
        // Clean and decode the private key from PEM format
        privateKey = privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decodedPrivateKey = Base64.getDecoder().decode(privateKey);

        // Convert the private key bytes into an RSAPrivateKey
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedPrivateKey);
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

        // Generate the certificate thumbprint (x5t)
        String thumbprint = getThumbprint(certificate);

        // Build the JWT header with the certificate thumbprint
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "RS256");
        header.put("typ", "JWT");
        header.put("x5t#S256", thumbprint);

        // Create the JWT with claims
        return JWT.create()
                .withHeader(header)
                .withClaim("aud", credentials.getOauth2Url())
                .withClaim("iss", credentials.getOauth2ClientId())
                .withClaim("sub", credentials.getOauth2ClientId())
                .withClaim("jti", UUID.randomUUID().toString())
                .withClaim("nbf", new Date().getTime() / 1000)  // Current time in seconds
                .withClaim("exp", (new Date().getTime() / 1000) + 600)  // Expiry time (10 minutes)
                .sign(Algorithm.RSA256(null, rsaPrivateKey));  // Sign the JWT with the RSA private key

    }

    private String getThumbprint(String certificatePem) throws Exception {
        // Remove the "BEGIN CERTIFICATE" and "END CERTIFICATE" lines and any extra whitespace
        String cleanedCert = certificatePem.replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replaceAll("\\s+", "");

        // Decode the Base64-encoded certificate string
        byte[] decodedCert = Base64.getDecoder().decode(cleanedCert);

        // Create a CertificateFactory instance for X.509 certificates
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

        // Generate the X509Certificate from the decoded bytes
        ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedCert);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] thumbprint = digest.digest(certificateFactory.generateCertificate(inputStream).getEncoded());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(thumbprint);
    }

    private MultiValueMap<String, String> buildRequestWithSharedSecret() {
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", credentials.getOauth2GrantType());
        requestBody.add("client_id", credentials.getOauth2ClientId());
        requestBody.add("client_secret", credentials.getOauth2ClientSecret());
        requestBody.add("scope", credentials.getOauth2Scope());
        return requestBody;
    }

    private String generateOauth2CacheKey() {
        return credentials.getBlindataTenantUuid() + "-" + credentials.getOauth2ClientId() + "-" + credentials.getOauth2Scope();
    }
}