package org.opendatamesh.platform.up.metaservice.blindata.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BdProbesUploadConfig {
    @Value("${blindata.probesUpload.connectionNamePropertyKey:x-blindataConnectionName}")
    private String connectionNamePropertyKey;

    public String getConnectionNamePropertyKey() {
        return connectionNamePropertyKey;
    }
}
