package org.opendatamesh.platform.up.metaservice.blindata.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
    private Optional<String> roleUuid;

    @Value("${blindata.odmPlatformUrl}")
    private String odmPlatformUrl;

    @Value("${blindata.systemNameRegex}")
    private Optional<String> systemNameRegex;

    @Value("${blindata.systemTechnologyRegex}")
    private Optional<String> systemTechnologyRegex;

    public String getOdmPlatformUrl() {
        return odmPlatformUrl;
    }

    public void setOdmPlatformUrl(String odmPlatformUrl) {
        this.odmPlatformUrl = odmPlatformUrl;
    }

    public Optional<String> getSystemNameRegex() {
        return systemNameRegex;
    }

    public void setSystemNameRegex(Optional<String> systemNameRegex) {
        this.systemNameRegex = systemNameRegex;
    }

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

    public Optional<String> getRoleUuid() {
        return roleUuid;
    }

    public void setRoleUuid(Optional<String> roleUuid) {
        this.roleUuid = roleUuid;
    }

    public Optional<String> getSystemTechnologyRegex() {
        return systemTechnologyRegex;
    }

    public void setSystemTechnologyRegex(Optional<String> systemTechnologyRegex) {
        this.systemTechnologyRegex = systemTechnologyRegex;
    }
}
