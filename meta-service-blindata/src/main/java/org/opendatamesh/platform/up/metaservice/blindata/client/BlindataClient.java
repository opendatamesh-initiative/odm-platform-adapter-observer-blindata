package org.opendatamesh.platform.up.metaservice.blindata.client;

import org.opendatamesh.platform.up.metaservice.blindata.resources.BlindataDataProductRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.ShortUserRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.StewardshipResponsibilityRes;
import org.opendatamesh.platform.up.metaservice.blindata.resources.StewardshipRoleRes;
import org.opendatamesh.platform.up.metaservice.services.MetaServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Component

public class BlindataClient {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(getClass());


    public BlindataClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    //Responsibilities method
    public StewardshipResponsibilityRes getResponsibility(String userUuid, String resourceIdentifier, String roleUuid, BlindataCredentials credentials) throws MetaServiceException {
        try {
            ResponseEntity<ResponsibilitiesPage> getResponsibility = restTemplate.exchange(
                    String.format("%s/api/v1/stewardship/responsibilities?userUuid=%s&resourceIdentifier=%s&roleUuid=%s", credentials.getBlindataUrl(), userUuid, resourceIdentifier, roleUuid),
                    HttpMethod.GET,
                    getHttpEntity(null, credentials),
                    ResponsibilitiesPage.class);
            return extractResponsibility(getResponsibility);
        } catch (HttpClientErrorException e) {
            throw new MetaServiceException("Unable to get responsibility: " + e.getResponseBodyAsString());
        }
    }

    public StewardshipResponsibilityRes createResponsibility(StewardshipResponsibilityRes responsibilityRes, BlindataCredentials credentials) throws MetaServiceException {
        try {
            ResponseEntity<StewardshipResponsibilityRes> postResponsibility = restTemplate.exchange(
                    String.format("%s/api/v1/stewardship/responsibilities", credentials.getBlindataUrl()),
                    HttpMethod.POST,
                    getHttpEntity(responsibilityRes, credentials),
                    StewardshipResponsibilityRes.class);
            return extractBody(postResponsibility, "Unable to create responsibility");
        } catch (HttpClientErrorException e) {
            throw new MetaServiceException("Unable to create responsibility: " + e.getResponseBodyAsString());
        }
    }

    //Users methods

    public ShortUserRes getBlindataUser(String username, BlindataCredentials credentials) throws MetaServiceException {
        try {
            ResponseEntity<UserPage> getUser = restTemplate.exchange(
                    String.format("%s/api/v1/users?tenantUuid=%s&search=%s", credentials.getBlindataUrl(), credentials.getBlindataTenantUuid(), username),
                    HttpMethod.GET,
                    getHttpEntity(null, credentials),
                    UserPage.class);
            return extractUser(getUser);
        } catch (HttpClientErrorException e) {
            throw new MetaServiceException("Unable to retrieve user: " + e.getResponseBodyAsString());
        }
    }

    //Role methods
    public StewardshipRoleRes getRole(String roleUuid, BlindataCredentials credentials) throws MetaServiceException {
        try {
            ResponseEntity<StewardshipRoleRes> getStatementResponse = restTemplate.exchange(
                    String.format("%s/api/v1/stewardship/roles/%s", credentials.getBlindataUrl(), roleUuid),
                    HttpMethod.GET,
                    getHttpEntity(null, credentials),
                    StewardshipRoleRes.class);
            return extractBody(getStatementResponse, "Unable to retriev role");
        } catch (HttpClientErrorException e) {
            throw new MetaServiceException("Unable to get data product: " + e.getResponseBodyAsString());
        }
    }

    //Data product methods

    public BlindataDataProductRes createDataProduct(BlindataDataProductRes blindataDataProductRes, BlindataCredentials credentials) throws MetaServiceException {
        try {
            ResponseEntity<BlindataDataProductRes> postStatementResponse = restTemplate.exchange(
                    String.format("%s/api/v1/dataproducts", credentials.getBlindataUrl()),
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
                throw new MetaServiceException("Unable to create data product: " + e.getResponseBodyAsString());
            }
        }
    }

    public BlindataDataProductRes updateDataProduct(BlindataDataProductRes blindataDataProductRes, BlindataCredentials credentials) throws MetaServiceException {
        try {
            ResponseEntity<BlindataDataProductRes> updateDataProduct = restTemplate.exchange(
                    String.format("%s/api/v1/dataproducts/%s", credentials.getBlindataUrl(), blindataDataProductRes.getUuid()),
                    HttpMethod.PUT,
                    getHttpEntity(blindataDataProductRes, credentials),
                    BlindataDataProductRes.class);
            return extractBody(updateDataProduct, "Unable to update data product");
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                logger.warn(e.getMessage());
                throw e;
            } else {
                logger.error(e.getMessage());
                throw new MetaServiceException("Unable to update data product: " + e.getResponseBodyAsString());
            }
        }
    }

    public BlindataDataProductRes getDataProduct(String dataProductIdentifier, BlindataCredentials credentials) throws MetaServiceException {
        try {
            ResponseEntity<BlindataDataProductPage> getStatementResponse = restTemplate.exchange(
                    String.format("%s/api/v1/dataproducts?page=0&size=1&identifier=%s", credentials.getBlindataUrl(), dataProductIdentifier),
                    HttpMethod.GET,
                    getHttpEntity(null, credentials),
                    BlindataDataProductPage.class);
            return extractDataproduct(getStatementResponse);
        } catch (HttpClientErrorException e) {
            throw new MetaServiceException("Unable to get data product: " + e.getResponseBodyAsString());
        }
    }

    public void deleteDataProduct(String dataProductIdentifier, BlindataCredentials credentials) throws MetaServiceException {
        try {
            restTemplate.exchange(
                    String.format("%s/api/v1/dataproducts/%s", credentials.getBlindataUrl(), dataProductIdentifier),
                    HttpMethod.DELETE,
                    getHttpEntity(null, credentials),
                    BlindataDataProductRes.class);
        } catch (HttpClientErrorException e) {
            throw new MetaServiceException("Unable to delete data product: " + e.getResponseBodyAsString());
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
        headers.set("X-BD-Tenant", credential.getBlindataTenantUuid());
        headers.set("X-BD-User", credential.getBlindataUsername());
        headers.set("X-BD-ApiKey", credential.getBlindataPass());
        return new HttpEntity<>(body, headers);
    }

    private <T> T extractBody(ResponseEntity<T> response, String exceptionMessage) throws MetaServiceException {
        final T body = response.getBody();
        if (body == null) {
            throw new MetaServiceException("Blindata returned empty response body: " + exceptionMessage);
        }
        return body;
    }

    static class UserPage {
        public List<ShortUserRes> content;
    }

    static class BlindataDataProductPage {
        public List<BlindataDataProductRes> content;
    }

    static class ResponsibilitiesPage {
        public List<StewardshipResponsibilityRes> content;
    }

    private BlindataDataProductRes extractDataproduct(ResponseEntity<BlindataDataProductPage> getStatementResponse) {
        if (getStatementResponse.getBody() != null && !getStatementResponse.getBody().content.isEmpty()) {
            return getStatementResponse.getBody().content.get(0);
        }
        return null;
    }

    private StewardshipResponsibilityRes extractResponsibility(ResponseEntity<ResponsibilitiesPage> getStewardshipResponsibility) {
        if (!Objects.requireNonNull(getStewardshipResponsibility.getBody()).content.isEmpty()) {
            return getStewardshipResponsibility.getBody().content.get(0);
        }
        return null;
    }

    private ShortUserRes extractUser(ResponseEntity<UserPage> getUser) {
        if (getUser.getBody() != null && !getUser.getBody().content.isEmpty()) {
            return getUser.getBody().content.get(0);
        }
        return null;
    }
}
