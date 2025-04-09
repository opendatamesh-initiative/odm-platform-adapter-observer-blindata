package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtilsFactory;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.exceptions.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.exceptions.ClientResourceMappingException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.OdmExternalComponentResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

class OdmRegistryClientImpl implements OdmRegistryClient {
    private final String baseUrl;
    private final RestUtils restUtils;
    private final RestTemplate restTemplate;

    OdmRegistryClientImpl(RestTemplate restTemplate, String baseUrl) {
        this.restUtils = RestUtilsFactory.getRestUtils(restTemplate);
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<OdmExternalComponentResource> getApis(String apiName, String apiVersion) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(String.format("%s/api/v1/pp/registry/apis", baseUrl))
                    .queryParam("name", apiName)
                    .queryParam("version", apiVersion)
                    .build()
                    .toUriString();
            //This probably will change when refactored from List to Page
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    String.class
            );
            //Esoteric method to handle bad api signature
            return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(responseEntity.getBody(), new TypeReference<>() {
                    });

        } catch (ClientException | ClientResourceMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<OdmExternalComponentResource> getApi(String identifier) {
        String url = String.format("%s/api/v1/pp/registry/apis/{id}", baseUrl);

        OdmExternalComponentResource api = restUtils.get(url, null, identifier, OdmExternalComponentResource.class);
        return Optional.ofNullable(api);
    }

    @Override
    public JsonNode getDataProductVersion(String dataProductId, String versionNumber) {

        String url = UriComponentsBuilder.fromHttpUrl(String.format("%s/api/v1/pp/registry/products/{id}/versions/{number}", baseUrl))
                .queryParam("format", "normalized")
                .buildAndExpand(dataProductId, versionNumber)
                .toUriString();

        return restUtils.get(url, null, null, JsonNode.class);
    }
}
