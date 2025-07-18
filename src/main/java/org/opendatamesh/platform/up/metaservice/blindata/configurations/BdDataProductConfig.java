package org.opendatamesh.platform.up.metaservice.blindata.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BdDataProductConfig {
    @Value("${blindata.dataProducts.assetsCleanup:true}")
    private boolean assetsCleanup;

    @Value("${blindata.dataProducts.additionalPropertiesRegex:}")
    private String additionalPropertiesRegex;

    public boolean isAssetsCleanup() {
        return assetsCleanup;
    }

    public String getAdditionalPropertiesRegex() {
        return additionalPropertiesRegex;
    }
}

