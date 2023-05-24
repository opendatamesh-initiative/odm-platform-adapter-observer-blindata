package org.opendatamesh.platform.up.metaservice.blindata.client;

import org.opendatamesh.platform.up.metaservice.blindata.resources.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component

public class BlindataClient {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(getClass());


    public BlindataClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public StewardshipResponsibilityRes getResponsibility(String userUuid, String resourceIdentifier, String roleUuid, BlindataCredentials credentials) throws Exception {
        try {
            ResponseEntity<StewardshipResponsibilityRes> getResponsibility = restTemplate.exchange(
                    String.format("%s/api/v1/stewardship/responsibilities/*/activeResponsibility?userUuid=%s&resourceIdentifier=%s&roleUuid=%s", credentials.getBlindataURL(), userUuid, resourceIdentifier, roleUuid),
                    HttpMethod.GET,
                    getHttpEntity(null, credentials),
                    StewardshipResponsibilityRes.class);
            return getResponsibility.getBody() != null ? extractBody(getResponsibility, "Unable to retrieve responsibility") : null;
        } catch (HttpClientErrorException e) {
            logger.error(e.getMessage());
            throw new Exception("Unable to get responsibility: " + e.getResponseBodyAsString());
        }
    }

    public StewardshipResponsibilityRes createResponsibility(StewardshipResponsibilityRes responsibilityRes, BlindataCredentials credentials) throws Exception {
        try {
            ResponseEntity<StewardshipResponsibilityRes> postResponsibility = restTemplate.exchange(
                    String.format("%s/api/v1/stewardship/responsibilities", credentials.getBlindataURL()),
                    HttpMethod.POST,
                    getHttpEntity(responsibilityRes, credentials),
                    StewardshipResponsibilityRes.class);
            return extractBody(postResponsibility, "Unable to create responsibility");
        } catch (HttpClientErrorException e) {
            logger.error(e.getMessage());
            throw new Exception("Unable to create responsibility: " + e.getResponseBodyAsString());
        }
    }

    public ShortUserRes getBlindataUser(String username, BlindataCredentials credentials) throws Exception {
        try {
            ResponseEntity<ShortUserRes> getUser = restTemplate.exchange(
                    String.format("%s/api/v1/users/*/shortUser?tenantUuid=" + credentials.getTenantUUID() + "&search=" + username, credentials.getBlindataURL()),
                    HttpMethod.GET,
                    getHttpEntity(null, credentials),
                    ShortUserRes.class);
            return extractBody(getUser, "Unable to retrieve user");
        } catch (HttpClientErrorException e) {
            logger.error(e.getMessage());
            throw new Exception("Unable to retrieve user: " + e.getResponseBodyAsString());
        }
    }

    public StewardshipRoleRes getRole(String roleUuid, BlindataCredentials credentials) throws Exception {
        try {
            ResponseEntity<StewardshipRoleRes> getStatementResponse = restTemplate.exchange(
                    String.format("%s/api/v1/stewardship/roles/" + roleUuid, credentials.getBlindataURL()),
                    HttpMethod.GET,
                    getHttpEntity(null, credentials),
                    StewardshipRoleRes.class);
            return extractBody(getStatementResponse, "Unable to retriev role");
        } catch (HttpClientErrorException e) {
            logger.error(e.getMessage());
            throw new Exception("Unable to get data product: " + e.getResponseBodyAsString());
        }
    }

    public BlindataDataProductRes createDataProduct(BlindataDataProductRes blindataDataProductRes, BlindataCredentials credentials) throws Exception {
        try {
            ResponseEntity<BlindataDataProductRes> postStatementResponse = restTemplate.exchange(
                    String.format("%s/api/v1/dataproducts", credentials.getBlindataURL()),
                    HttpMethod.POST,
                    getHttpEntity(blindataDataProductRes, credentials),
                    BlindataDataProductRes.class);
            return extractBody(postStatementResponse, "Unable to create data product");
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                logger.warn(e.getMessage());
                throw e;
            } else {
                logger.error(e.getMessage());
                throw new Exception("Unable to create data product: " + e.getResponseBodyAsString());
            }
        }
    }

    public BlindataDataProductRes updateDataProduct(BlindataDataProductRes blindataDataProductRes, BlindataCredentials credentials) throws Exception {
        try {
            ResponseEntity<BlindataDataProductRes> postStatementResponse = restTemplate.exchange(
                    String.format("%s/api/v1/dataproducts/" + blindataDataProductRes.getUuid(), credentials.getBlindataURL()),
                    HttpMethod.PUT,
                    getHttpEntity(blindataDataProductRes, credentials),
                    BlindataDataProductRes.class);
            return extractBody(postStatementResponse, "Unable to update data product");
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                logger.warn(e.getMessage());
                throw e;
            } else {
                logger.error(e.getMessage());
                throw new Exception("Unable to update data product: " + e.getResponseBodyAsString());
            }
        }
    }

    public BlindataDataProductRes getDataProduct(String dataProductIdentifier, BlindataCredentials credentials) throws Exception {
        try {
            ResponseEntity<DataProductPage> getStatementResponse = restTemplate.exchange(
                    String.format("%s/api/v1/dataproducts?page=0&size=1&identifier=" + dataProductIdentifier, credentials.getBlindataURL()),
                    HttpMethod.GET,
                    getHttpEntity(null, credentials),
                    DataProductPage.class);
            return getStatementResponse.getBody().content.get(0);
        } catch (HttpClientErrorException e) {
            logger.error(e.getMessage());
            throw new Exception("Unable to get data product: " + e.getResponseBodyAsString());
        }
    }

    /*
    public SystemResource createSystem(SystemResource systemRes, BlindataCredentials credentials) throws Exception {
        try {
            ResponseEntity<SystemResource> postStatementResponse = restTemplate.exchange(
                    String.format("%s/api/v1/systems", credentials.getBlindataURL()),
                    HttpMethod.POST,
                    getHttpEntity(systemRes, credentials),
                    SystemResource.class);
            return extractBody(postStatementResponse, "Unable to create system");
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                logger.warn(e.getMessage());
                throw e;
            } else {
                logger.error(e.getMessage());
                throw new Exception("Unable to create system: " + e.getResponseBodyAsString());
            }
        }
    }


    public void deleteSystem(SystemResource system, BlindataCredentials credentials) throws Exception {
        try {
            restTemplate.exchange(
                    String.format("%s/api/v1/systems/" + system.getUuid(), credentials.getBlindataURL()),
                    HttpMethod.DELETE,
                    getHttpEntity(null, credentials),
                    SystemResource.class);
        } catch (HttpClientErrorException e) {
            logger.error(e.getMessage());
            throw new Exception("Unable to delete system: " + e.getResponseBodyAsString());
        }

    }

    public SystemResource getSystem(String uuid, BlindataCredentials credentials) throws Exception {
        try {
            ResponseEntity<SystemResource> getStatementResponse = restTemplate.exchange(
                    String.format("%s/api/v1/systems/" + uuid, credentials.getBlindataURL()),
                    HttpMethod.GET,
                    getHttpEntity(null, credentials),
                    SystemResource.class);
            return extractBody(getStatementResponse, "Unable to get system");
        } catch (HttpClientErrorException e) {
            logger.error(e.getMessage());
            throw new Exception("Unable to get system: " + e.getResponseBodyAsString());
        }
    }


    public SystemResource putSystem(SystemResource system, BlindataCredentials credentials) throws Exception {
        //get modifico put
        try {
            ResponseEntity<SystemResource> putResponse = restTemplate.exchange(String.format("%s/api/v1/systems/" + system.getUuid(), credentials.getBlindataURL()),
                    HttpMethod.PUT,
                    getHttpEntity(system, credentials),
                    SystemResource.class);
            return extractBody(putResponse, "Unable to get system");
        } catch (HttpClientErrorException e) {
            logger.error(e.getMessage());
            throw new Exception("Unable to get system: " + e.getResponseBodyAsString());
        }
    }


     */
    private <T> org.springframework.http.HttpEntity<T> getHttpEntity(T body, BlindataCredentials credential) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-BD-Tenant", credential.getTenantUUID());
        headers.set("X-BD-User", credential.getUser());
        headers.set("X-BD-ApiKey", credential.getPassword());
        return new HttpEntity<>(body, headers);
    }

    private <T> T extractBody(ResponseEntity<T> response, String exceptionMessage) throws Exception {
        final T body = response.getBody();
        if (body == null) {
            throw new Exception("Blindata returned empty response body: " + exceptionMessage);
        }
        return body;
    }

    static class DataProductPage {
        public List<BlindataDataProductRes> content;
    }
}
