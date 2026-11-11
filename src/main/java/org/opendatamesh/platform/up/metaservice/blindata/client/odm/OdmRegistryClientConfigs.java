package org.opendatamesh.platform.up.metaservice.blindata.client.odm;

import org.opendatamesh.platform.up.metaservice.blindata.utils.client.RestUtilsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Builds the Registry V2 client. It is real only with {@code odm.productPlane.registryService.active: true} and
 * {@code apiVersion: v2}; otherwise every read fails with a message saying Registry V2 is not configured, so the
 * DevOps 2.0 event that needed it is closed as failed (BDMD-5558).
 */
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

    @Bean
    public OdmRegistryClient odmRegistryClient() {
        String validatedApiVersion = validatedApiVersion();
        if (active && "V2".equals(validatedApiVersion)) {
            return new OdmRegistryClientImpl(RestUtilsFactory.getRestUtils(restTemplateBuilder.build()), address);
        }
        log.warn("ODM Registry V2 Client is not enabled in the configuration (active={}, apiVersion={}).", active, apiVersion);
        return dataProductVersionUuid -> {
            throw new IllegalStateException("ODM Registry V2 is not configured: set odm.productPlane.registryService.active to true and odm.productPlane.registryService.apiVersion to v2 to read data product version " + dataProductVersionUuid);
        };
    }

    private String validatedApiVersion() {
        String normalized = apiVersion == null ? null : apiVersion.toUpperCase();
        if (!"V1".equals(normalized) && !"V2".equals(normalized)) {
            throw new IllegalStateException("Unsupported odm.productPlane.registryService.apiVersion: '" + apiVersion + "'. Allowed values: v1, v2.");
        }
        return normalized;
    }
}
