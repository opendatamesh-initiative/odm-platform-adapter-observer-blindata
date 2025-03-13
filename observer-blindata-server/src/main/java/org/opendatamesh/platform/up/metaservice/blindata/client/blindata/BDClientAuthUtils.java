package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
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

public class BDClientAuthUtils {

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

    public BDClientAuthUtils(BDCredentials credentials) {
        this.credentials = credentials;
    }

    public HttpHeaders getAuthenticatedHttpHeaders(HttpHeaders headers) {
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
