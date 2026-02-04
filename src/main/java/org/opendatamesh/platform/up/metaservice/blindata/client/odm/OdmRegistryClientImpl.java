package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtilsFactory;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.exceptions.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.exceptions.ClientResourceMappingException;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v1.OdmExternalComponentResource;
import org.opendatamesh.platform.up.metaservice.blindata.schema_analyzers.utils.InternalRefResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(OdmRegistryClientImpl.class);

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
    public Optional<JsonNode> getApi(String identifier) {
        String url = String.format("%s/api/v1/pp/registry/apis/{id}", baseUrl);

        JsonNode api = restUtils.get(url, null, identifier, JsonNode.class);

        if (api != null && api.isObject() && api.has("definition")) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(api.get("definition").asText());
                InternalRefResolver.resolveRefs(rootNode, rootNode);
                ((ObjectNode) api).remove("definition");
                ((ObjectNode) api).set("definition", rootNode);
            } catch (JsonProcessingException e) {
                logger.warn("Failed to parse the JSON of port standard definition schema: {}", api);
            }
        }

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
