package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class OdmRegistryClientConfigs {
    @Value("${odm.productPlane.registryService.address}")
    private String address;

    @Value("${odm.productPlane.registryService.active}")
    private boolean active;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    OdmRegistryClient odmRegistryClient() {
        if (active) {
            return new OdmRegistryClientImpl(
                    restTemplate,
                    objectMapper,
                    address
            );
        } else {
            log.warn("ODM Registry Client is not enabled in the configuration.");
            return null;//TODO
        }
    }

}
