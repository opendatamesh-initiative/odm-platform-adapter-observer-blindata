package org.opendatamesh.platform.up.metaservice.blindata.client;

import org.opendatamesh.platform.up.metaservice.blindata.resources.SystemResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component

public class BlindataClient {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(getClass());
   

    public BlindataClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

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
                    String.format("%s/api/v1/systems/"+system.getUuid(), credentials.getBlindataURL()),
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
            ResponseEntity<SystemResource> getStatementResponse =restTemplate.exchange(
                    String.format("%s/api/v1/systems/"+uuid, credentials.getBlindataURL()),
                    HttpMethod.GET,
                    getHttpEntity(null, credentials),
                    SystemResource.class);
            return extractBody(getStatementResponse, "Unable to get system");
        } catch (HttpClientErrorException e) {
            logger.error(e.getMessage());
            throw new Exception("Unable to get system: " + e.getResponseBodyAsString());
        }
    }

    
    public SystemResource putSystem(SystemResource system, BlindataCredentials credentials) throws Exception{
        //get modifico put
        try {
        ResponseEntity<SystemResource> putResponse = restTemplate.exchange(String.format("%s/api/v1/systems/"+system.getUuid(), credentials.getBlindataURL()),
                HttpMethod.PUT,
                getHttpEntity(system, credentials),
                SystemResource.class);
            return extractBody(putResponse, "Unable to get system");
        } catch (HttpClientErrorException e) {
            logger.error(e.getMessage());
            throw new Exception("Unable to get system: " + e.getResponseBodyAsString());
        }
    }

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


}
