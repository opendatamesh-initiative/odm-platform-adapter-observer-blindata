package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BDCredentials {
    @Value("${blindata.url}")
    private String blindataUrl;
    @Value("${blindata.user}")
    private String blindataUsername;
    @Value("${blindata.password}")
    private String blindataPass;
    @Value("${blindata.tenantUUID}")
    private String blindataTenantUuid;
    @Value("${blindata.roleUuid}")
    private String roleUuid;


    public String getBlindataUrl() {
        return blindataUrl;
    }

    public String getBlindataUsername() {
        return blindataUsername;
    }

    public String getBlindataPass() {
        return blindataPass;
    }

    public String getBlindataTenantUuid() {
        return blindataTenantUuid;
    }

    public String getRoleUuid() {
        return roleUuid;
    }

}
