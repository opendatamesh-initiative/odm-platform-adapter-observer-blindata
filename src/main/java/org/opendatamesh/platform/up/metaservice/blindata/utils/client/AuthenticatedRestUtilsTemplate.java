package org.opendatamesh.platform.up.metaservice.blindata.utils.client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.up.metaservice.blindata.exceptions.client.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.utils.client.http.HttpEntity;
import org.opendatamesh.platform.up.metaservice.blindata.utils.client.http.HttpHeader;
import org.opendatamesh.platform.up.metaservice.blindata.utils.client.http.HttpMethod;
import org.opendatamesh.platform.up.metaservice.blindata.utils.client.http.MediaType;
import org.opendatamesh.platform.up.metaservice.blindata.utils.client.http.Oauth2;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class AuthenticatedRestUtilsTemplate implements RestUtilsTemplate {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ConcurrentHashMap<String, CachedToken> TOKEN_CACHE = new ConcurrentHashMap<>();

    private final List<HttpHeader> connectionHeaders = new ArrayList<>();
    private final RestUtilsTemplate wrappedInstance;
    private final Oauth2 oauth2;

    AuthenticatedRestUtilsTemplate(
            RestUtilsTemplate restUtils,
            List<HttpHeader> connectionHeaders,
            Oauth2 oauth2) {
        this.wrappedInstance = restUtils;
        if (connectionHeaders != null) {
            this.connectionHeaders.addAll(connectionHeaders);
        }
        this.oauth2 = oauth2;
    }

    private List<HttpHeader> getAuthenticatedHeaders(List<HttpHeader> headers) {
        List<HttpHeader> authenticatedHeaders = headers != null ? new ArrayList<>(headers) : new ArrayList<>();
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
        CachedToken cached = TOKEN_CACHE.get(generateOauth2CacheKey());
        if (cached != null && Instant.now().isBefore(cached.expiry())) {
            return Optional.of(cached.token());
        }
        return Optional.empty();
    }

    private String retrieveOauth2Token() {
        if (oauth2 == null) {
            throw new IllegalStateException("OAuth2 parameters are missing.");
        }

        MultiValueMap<String, String> requestBody = StringUtils.hasText(oauth2.getClientSecret())
                ? buildRequestWithSharedSecret(oauth2)
                : buildRequestWithCertificate(oauth2);

        List<HttpHeader> tokenRequestHeaders = new ArrayList<>();
        tokenRequestHeaders.add(HttpHeader.contentType(MediaType.APPLICATION_FORM_URLENCODED));
        try {
            ObjectNode responseBody = wrappedInstance.exchange(
                    oauth2.getUrl(),
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody, tokenRequestHeaders),
                    ObjectNode.class,
                    new HashMap<>()
            );

            if (responseBody != null && responseBody.has("access_token")) {
                String accessToken = responseBody.get("access_token").asText();
                int expiresIn = responseBody.has("expires_in") ? responseBody.get("expires_in").asInt(3600) : 3600;
                Instant expiryTime = Instant.now().plusSeconds(expiresIn);
                TOKEN_CACHE.put(generateOauth2CacheKey(), new CachedToken(accessToken, expiryTime));
                return accessToken;
            }
            if (responseBody != null && responseBody.has("error_description")) {
                throw new IllegalStateException(responseBody.get("error_description").asText());
            }
            if (responseBody != null && responseBody.has("error")) {
                throw new IllegalStateException(responseBody.get("error").asText());
            }
            throw new IllegalStateException("Missing access_token field on response body.");
        } catch (ClientException e) {
            throw new IllegalStateException(resolveOauth2TokenErrorMessage(e), e);
        }
    }

    private String resolveOauth2TokenErrorMessage(ClientException e) {
        String responseBody = e.getResponseBody();
        if (!StringUtils.hasText(responseBody)) {
            return "OAuth2 token request failed with HTTP " + e.getCode();
        }
        try {
            ObjectNode errorNode = (ObjectNode) OBJECT_MAPPER.readTree(responseBody);
            if (errorNode.hasNonNull("error_description")) {
                return errorNode.get("error_description").asText();
            }
            if (errorNode.hasNonNull("error")) {
                return errorNode.get("error").asText();
            }
        } catch (Exception ignored) {
            // fall through to raw body
        }
        return responseBody;
    }

    private MultiValueMap<String, String> buildRequestWithCertificate(Oauth2 oauth2Params) {
        LinkedMultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", oauth2Params.getGrantType());
        requestBody.add("client_id", oauth2Params.getClientId());
        requestBody.add("scope", oauth2Params.getScope());
        requestBody.add("client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer");
        try {
            requestBody.add("client_assertion", generateClientAssertionWithCertificate());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return requestBody;
    }

    private String generateClientAssertionWithCertificate() throws Exception {
        String privateKey = oauth2.getClientCertificatePrivateKey();
        String certificate = oauth2.getClientCertificate();
        privateKey = privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] decodedPrivateKey = Base64.getDecoder().decode(privateKey);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedPrivateKey);
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);

        String thumbprint = getThumbprint(certificate);

        Map<String, Object> header = new HashMap<>();
        header.put("alg", "RS256");
        header.put("typ", "JWT");
        header.put("x5t#S256", thumbprint);

        return JWT.create()
                .withHeader(header)
                .withClaim("aud", oauth2.getUrl())
                .withClaim("iss", oauth2.getClientId())
                .withClaim("sub", oauth2.getClientId())
                .withClaim("jti", UUID.randomUUID().toString())
                .withClaim("nbf", new Date().getTime() / 1000)
                .withClaim("exp", (new Date().getTime() / 1000) + 600)
                .sign(Algorithm.RSA256(null, rsaPrivateKey));
    }

    private String getThumbprint(String certificatePem) throws Exception {
        String cleanedCert = certificatePem.replace("-----BEGIN CERTIFICATE-----", "")
                .replace("-----END CERTIFICATE-----", "")
                .replaceAll("\\s+", "");
        byte[] decodedCert = Base64.getDecoder().decode(cleanedCert);
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedCert);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] thumbprint = digest.digest(certificateFactory.generateCertificate(inputStream).getEncoded());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(thumbprint);
    }

    private MultiValueMap<String, String> buildRequestWithSharedSecret(Oauth2 oauth2Params) {
        LinkedMultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("grant_type", oauth2Params.getGrantType());
        requestBody.add("client_id", oauth2Params.getClientId());
        requestBody.add("client_secret", oauth2Params.getClientSecret());
        requestBody.add("scope", oauth2Params.getScope());
        return requestBody;
    }

    private String generateOauth2CacheKey() {
        return oauth2.getUrl() + "-" + oauth2.getClientId() + "-" + oauth2.getScope();
    }

    @Override
    public <T> T exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType, Map<String, ?> uriVariables)
            throws ClientException {
        HttpEntity<?> updatedRequestEntity = new HttpEntity<>(
                requestEntity.getBody(),
                getAuthenticatedHeaders(requestEntity.getHeaders())
        );
        return wrappedInstance.exchange(url, method, updatedRequestEntity, responseType, uriVariables);
    }

    @Override
    public File download(String url, List<HttpHeader> httpHeaders, Object resource, File storeLocation) throws ClientException {
        return wrappedInstance.download(url, getAuthenticatedHeaders(httpHeaders), resource, storeLocation);
    }

    private record CachedToken(String token, Instant expiry) {
    }
}
