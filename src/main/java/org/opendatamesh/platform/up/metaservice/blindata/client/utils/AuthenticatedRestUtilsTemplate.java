package org.opendatamesh.platform.up.metaservice.blindata.client.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

class AuthenticatedRestUtilsTemplate implements RestUtilsTemplate {

    private final List<HttpHeader> connectionHeaders = new ArrayList<>();
    private final RestUtilsTemplate wrappedInstance;
    private final Oauth2 oauth2;

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


    public AuthenticatedRestUtilsTemplate(
            RestUtilsTemplate restUtils,
            List<HttpHeader> connectionHeaders,
            Oauth2 oauth2) {
        this.wrappedInstance = restUtils;
        if (connectionHeaders != null) {
            this.connectionHeaders.addAll(connectionHeaders);
        }
        this.oauth2 = oauth2;
    }

    /**
     * Retrieves the appropriate HTTP headers for authenticated requests.
     * Uses either API keys or OAuth2 tokens based on the configuration.
     *
     * @param headers Optional existing headers to add to.
     * @return The HTTP headers with authentication information.
     * @throws IllegalStateException If OAuth2 token retrieval fails.
     */
    private List<HttpHeader> getAuthenticatedHeaders(List<HttpHeader> headers) {
        List<HttpHeader> authenticatedHeaders = headers != null ? headers : new ArrayList<>();
        for (HttpHeader header : connectionHeaders) {
            authenticatedHeaders.add(new HttpHeader(header.getName(), header.getValue()));
        }

        if (oauth2 != null) {
            String accessToken = getCachedOauth2Token().orElseGet(this::retrieveOauth2Token);
            if (accessToken != null) {
                authenticatedHeaders.add(HttpHeader.bearerAuth(accessToken));
            } else {
                throw new IllegalStateException("Failed to retrieve OAuth2 token.");
            }
        }
        return authenticatedHeaders;
    }

    private synchronized Optional<String> getCachedOauth2Token() {
        Instant expiryTime = tokenExpiryCache.getIfPresent(generateOauth2CacheKey());
        if (expiryTime != null && Instant.now().isBefore(expiryTime)) {
            return Optional.ofNullable(tokenCache.getIfPresent(generateOauth2CacheKey()));
        }
        return Optional.empty();
    }

    private String retrieveOauth2Token() {
        if (oauth2 == null) {
            throw new IllegalStateException("OAuth2 parameters are missing.");
        }

        Map<String, String> requestBody = StringUtils.hasText(oauth2.getClientSecret()) ?
                buildRequestWithSharedSecret(oauth2) :
                buildRequestWithCertificate(oauth2);

        List<HttpHeader> tokenRequestHeaders = new ArrayList<>();
        tokenRequestHeaders.add(HttpHeader.contentType(MediaType.APPLICATION_FORM_URLENCODED));
        try {
            ObjectNode responseBody = wrappedInstance.exchange(
                    oauth2.getUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody, tokenRequestHeaders),
                    ObjectNode.class
            );

            if (responseBody != null && responseBody.has("access_token")) {
                String accessToken = responseBody.get("access_token").asText();
                int expiresIn = responseBody.has("expires_in") ? responseBody.get("expires_in").asInt(3600) : 3600;
                Instant expiryTime = Instant.now().plusSeconds(expiresIn);
                String cacheKey = generateOauth2CacheKey();
                tokenCache.put(cacheKey, accessToken);
                tokenExpiryCache.put(cacheKey, expiryTime);
                return accessToken;
            } else {
                throw new IllegalStateException("Missing access_token field on response body.");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error while retrieving OAuth2 token: " + e.getMessage(), e);
        }
    }

    private Map<String, String> buildRequestWithCertificate(Oauth2 oauth2Params) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", oauth2Params.getGrantType());
        requestBody.put("client_id", oauth2Params.getClientId());
        requestBody.put("scope", oauth2Params.getScope());
        requestBody.put("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        try {
            String clientAssertion = generateClientAssertionWithCertificate();
            requestBody.put("client_assertion", clientAssertion);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return requestBody;
    }

    private String generateClientAssertionWithCertificate() throws Exception {
        String privateKey = oauth2.getClientCertificatePrivateKey();
        String certificate = oauth2.getClientCertificate();
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
                .withClaim("aud", oauth2.getUrl())
                .withClaim("iss", oauth2.getClientId())
                .withClaim("sub", oauth2.getClientId())
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

    private Map<String, String> buildRequestWithSharedSecret(Oauth2 oauth2Params) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("grant_type", oauth2Params.getGrantType());
        requestBody.put("client_id", oauth2Params.getClientId());
        requestBody.put("client_secret", oauth2Params.getClientSecret());
        requestBody.put("scope", oauth2Params.getScope());
        return requestBody;
    }

    private String generateOauth2CacheKey() {
        return oauth2.getUrl() + "-" + oauth2.getClientId() + "-" + oauth2.getScope();
    }

    /* =====================================================================================
     *       Wrapped Methods
     *  ======================================================================================
     */

    @Override
    public <T> T exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Object... uriVariables) throws RestClientException {
        HttpEntity<?> updatedRequestEntity = new HttpEntity<>(
                requestEntity.getBody(),
                getAuthenticatedHeaders(requestEntity.getHeaders())
        );
        return wrappedInstance.exchange(url, method, updatedRequestEntity, responseType, uriVariables);
    }

    @Override
    public <T> T exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
        HttpEntity<?> updatedRequestEntity = new HttpEntity<>(
                requestEntity.getBody(),
                getAuthenticatedHeaders(requestEntity.getHeaders())
        );
        return wrappedInstance.exchange(url, method, updatedRequestEntity, responseType, uriVariables);
    }

    @Override
    public File download(String url, List<HttpHeader> httpHeaders, Object resource, File storeLocation) throws RestClientException {
        return wrappedInstance.download(url, getAuthenticatedHeaders(httpHeaders), resource, storeLocation);
    }
}
