package org.opendatamesh.platform.up.metaservice.blindata.client.odm;


import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.v1.OdmExternalComponentResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Configuration
public class OdmRegistryClientConfigs {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${odm.productPlane.registryService.address}")
    private String address;

    @Value("${odm.productPlane.registryService.active}")
    private boolean active;

    @Value("${odm.productPlane.registryService.apiVersion:v1}")
    private String apiVersion;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    private RestTemplate getRestTemplate() {
        return restTemplateBuilder.build();
    }

    @Bean
    public OdmRegistryClient odmRegistryClient() {
        if (active) {
            if (apiVersion.toUpperCase().equals("V1")) {
                return new OdmRegistryClientImpl(
                        getRestTemplate(),
                        address
                );
            } else {
                return new OdmRegistryClient() {
                    @Override
                    public List<OdmExternalComponentResource> getApis(String apiName, String apiVersion) {
                        log.warn("unused method");
                        return List.of();
                    }
    
                    @Override
                    public Optional<JsonNode> getApi(String identifier) {
                        log.warn("using registry V2 this method should never be called, descriptor should always be available in full, but it was called for API with id: {}.", identifier);
                        return Optional.empty();
                    }
    
                    @Override
                    public JsonNode getDataProductVersion(String dataProductId, String versionNumber) {
                        log.warn("unused method");
                        return null;
                    }
                };
            }
        } else {
            log.warn("ODM Registry Client is not enabled in the configuration.");
            return new OdmRegistryClient() {
                @Override
                public List<OdmExternalComponentResource> getApis(String apiName, String apiVersion) {
                    log.warn("getApis called but registry client is disabled. Returning empty list for API {} version {}.", apiName, apiVersion);
                    return List.of();
                }

                @Override
                public Optional<JsonNode> getApi(String identifier) {
                    log.warn("getApi called but registry client is disabled. Returning optional.empty() for API with id: {}.", identifier);
                    return Optional.empty();
                }

                @Override
                public JsonNode getDataProductVersion(String dataProductId, String versionNumber) {
                    log.warn("getDataProductVersion called but registry client is disabled. No data product version found for id {} version {}.", dataProductId, versionNumber);
                    return null;
                }
            };
        }
    }
}
