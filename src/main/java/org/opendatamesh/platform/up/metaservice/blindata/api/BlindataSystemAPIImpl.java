package org.opendatamesh.platform.up.metaservice.blindata.api;

import org.opendatamesh.platform.up.metaservice.blindata.entities.BlindataSystem;
import org.opendatamesh.platform.up.metaservice.blindata.entities.Credentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class BlindataSystemAPIImpl implements BlindataSystemAPI{

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RestTemplate restTemplate;

    public BlindataSystemAPIImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public BlindataSystem postSystem(BlindataSystem system, Credentials credentials) throws Exception {
        try {
            ResponseEntity<BlindataSystem> postStatementResponse = restTemplate.exchange(
                    String.format("%s/api/v1/systems", credentials.getBlindataURL()),
                    HttpMethod.POST,
                    getHttpEntity(system, credentials),
                    BlindataSystem.class);
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

    @Override
    public void deleteSystem(BlindataSystem system, Credentials credentials) throws Exception {
        try {
            restTemplate.exchange(
                    String.format("%s/api/v1/systems/"+system.getUuid(), credentials.getBlindataURL()),
                    HttpMethod.DELETE,
                    getHttpEntity(null, credentials),
                    BlindataSystem.class);
        } catch (HttpClientErrorException e) {
            logger.error(e.getMessage());
            throw new Exception("Unable to delete system: " + e.getResponseBodyAsString());
        }

    }

    @Override
    public BlindataSystem getSystem(String uuid, Credentials credentials) throws Exception {
        try {
            ResponseEntity<BlindataSystem> getStatementResponse =restTemplate.exchange(
                    String.format("%s/api/v1/systems/"+uuid, credentials.getBlindataURL()),
                    HttpMethod.GET,
                    getHttpEntity(null, credentials),
                    BlindataSystem.class);
            return extractBody(getStatementResponse, "Unable to get system");
        } catch (HttpClientErrorException e) {
            logger.error(e.getMessage());
            throw new Exception("Unable to get system: " + e.getResponseBodyAsString());
        }
    }
    @Override
    public BlindataSystem putSystem(BlindataSystem system, Credentials credentials) throws Exception{
        //get modifico put
        try {
        ResponseEntity<BlindataSystem> putResponse = restTemplate.exchange(String.format("%s/api/v1/systems/"+system.getUuid(), credentials.getBlindataURL()),
                HttpMethod.PUT,
                getHttpEntity(system, credentials),
                BlindataSystem.class);
            return extractBody(putResponse, "Unable to get system");
        } catch (HttpClientErrorException e) {
            logger.error(e.getMessage());
            throw new Exception("Unable to get system: " + e.getResponseBodyAsString());
        }
    }

    private <T> org.springframework.http.HttpEntity<T> getHttpEntity(T body, Credentials credential) {
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
