package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.ClientException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.ClientResourceMappingException;
import org.opendatamesh.platform.up.metaservice.blindata.client.utils.RestUtils;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.OdmExternalComponentResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

class OdmRegistryClientImpl implements OdmRegistryClient {
    private final String baseUrl;
    private final RestUtils restUtils;
    private final RestTemplate restTemplate;

    OdmRegistryClientImpl(RestTemplate restTemplate, String baseUrl) {
        this.restUtils = new RestUtils(restTemplate);
        this.baseUrl = baseUrl;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<OdmExternalComponentResource> getApi(String apiName, String apiVersion) {
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
            throw new RuntimeException(e); //TODO
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataProductVersionDPDS getDataProductVersion(String dataProductId, String versionNumber) {

        String url = UriComponentsBuilder.fromHttpUrl(String.format("%s/api/v1/pp/registry/products/{id}/versions/{number}", baseUrl))
                .queryParam("format", "normalized")
                .buildAndExpand(dataProductId, versionNumber)
                .toUriString();

        return restUtils.get(url, null, null, DataProductVersionDPDS.class);
    }
}
