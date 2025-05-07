package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BDDataProductConfig {
    @Value("${blindata.dataProducts.assetsCleanup:true}")
    private boolean assetsCleanup;

    public boolean isAssetsCleanup() {
        return assetsCleanup;
    }
}

