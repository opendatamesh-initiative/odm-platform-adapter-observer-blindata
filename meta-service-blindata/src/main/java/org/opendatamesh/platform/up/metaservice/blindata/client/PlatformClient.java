package org.opendatamesh.platform.up.metaservice.blindata.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.resources.schema.SchemaRes;
import org.opendatamesh.platform.up.metaservice.server.services.MetaServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class PlatformClient {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    ObjectMapper objectMapper;

    public PlatformClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    //Get array schemas ID
    public List<Integer> getSchemasId(String portID, BlindataCredentials blindataCredentials) throws MetaServiceException {
        try {
            return restTemplate.exchange(
                    String.format("%s/api/v1/pp/registry/apis/%s/endpoints", blindataCredentials.getOdmPlatformUrl(), portID),
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<List<Integer>>() {
                    }).getBody();
        } catch (HttpClientErrorException e) {
            throw new MetaServiceException("Unable to get array of schemas ID: " + e.getResponseBodyAsString());
        }
    }

    //Get Schema from given ID
    public String getSchemaContent(Integer schemaID, BlindataCredentials blindataCredentials) throws MetaServiceException {
        try {
            String responseEntity = restTemplate.exchange(
                    String.format("%s/api/v1/pp/registry/schemas/%d", blindataCredentials.getOdmPlatformUrl(), schemaID),
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    String.class).getBody();
            return objectMapper.readValue(responseEntity, SchemaRes.class).getContent();
        } catch (HttpClientErrorException e) {
            throw new MetaServiceException("Unable to get array of schemas ID: " + e.getResponseBodyAsString());
        } catch (JsonMappingException e) {
            throw new MetaServiceException("Unable to map schema resource: " + e.getMessage());
        } catch (JsonProcessingException e) {
            throw new MetaServiceException("Unable to process schema resource:" + e.getMessage());
        }
    }
}
