package org.opendatamesh.platform.up.metaservice.blindata.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BlindataCredentials {


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

    public void setBlindataUrl(String blindataUrl) {
        this.blindataUrl = blindataUrl;
    }

    public String getBlindataUsername() {
        return blindataUsername;
    }

    public void setBlindataUsername(String blindataUsername) {
        this.blindataUsername = blindataUsername;
    }

    public String getBlindataPass() {
        return blindataPass;
    }

    public void setBlindataPass(String blindataPass) {
        this.blindataPass = blindataPass;
    }

    public String getBlindataTenantUuid() {
        return blindataTenantUuid;
    }

    public void setBlindataTenantUuid(String blindataTenantUuid) {
        this.blindataTenantUuid = blindataTenantUuid;
    }

    public String getRoleUuid() {
        return roleUuid;
    }

    public void setRoleUuid(String roleUuid) {
        this.roleUuid = roleUuid;
    }
}
