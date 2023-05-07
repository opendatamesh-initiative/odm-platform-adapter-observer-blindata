package org.opendatamesh.platform.up.metaservice.blindata.entities;

public class Credentials {
    private String blindataURL;
    private String user;
    private String password;
    private String tenantUUID;

    public Credentials(String blindataURL, String user, String password, String tenantUUID) {
        this.blindataURL = blindataURL;
        this.user = user;
        this.password = password;
        this.tenantUUID = tenantUUID;
    }

    public String getBlindataURL() {
        return blindataURL;
    }

    public void setBlindataURL(String blindataURL) {
        this.blindataURL = blindataURL;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTenantUUID() {
        return tenantUUID;
    }

    public void setTenantUUID(String tenantUUID) {
        this.tenantUUID = tenantUUID;
    }
}
