package org.opendatamesh.platform.up.metaservice.blindata.client.odm;


import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.up.metaservice.blindata.resources.odm.registry.OdmExternalComponentResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public OdmRegistryClient odmRegistryClient() {
        if (active) {
            return new OdmRegistryClientImpl(
                    restTemplate,
                    address
            );
        } else {
            log.warn("ODM Registry Client is not enabled in the configuration.");
            return new OdmRegistryClient() {
                @Override
                public List<OdmExternalComponentResource> getApis(String apiName, String apiVersion) {
                    log.warn("getApis called but registry client is disabled. Returning empty list for API {} version {}.", apiName, apiVersion);
                    return List.of();
                }

                @Override
                public Optional<OdmExternalComponentResource> getApi(String identifier) {
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
