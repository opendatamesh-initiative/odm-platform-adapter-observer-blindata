package org.opendatamesh.platform.up.metaservice.blindata.client.blindata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BdCredentials {
    @Value("${blindata.enableAsync:false}")
    private boolean enableAsync;

    @Value("${blindata.url}")
    private String blindataUrl;
    @Value("${blindata.roleUuid}")
    private String roleUuid;

    @Value("${blindata.user:}")
    private String blindataUsername;
    @Value("${blindata.password:}")
    private String blindataPass;
    @Value("${blindata.tenantUUID}")
    private String blindataTenantUuid;

    @Value("${blindata.oauth2.url:}")
    private String oauth2Url;
    @Value("${blindata.oauth2.grantType:}")
    private String oauth2GrantType;
    @Value("${blindata.oauth2.scope:}")
    private String oauth2Scope;
    @Value("${blindata.oauth2.clientId:}")
    private String oauth2ClientId;
    @Value("${blindata.oauth2.clientSecret:}")
    private String oauth2ClientSecret;
    @Value("${blindata.oauth2.clientCertificate:}")
    private String oauth2ClientCertificate;
    @Value("${blindata.oauth2.clientCertificatePrivateKey:}")
    private String oauth2ClientCertificatePrivateKey;

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

    public String getOauth2Url() {
        return oauth2Url;
    }

    public String getOauth2GrantType() {
        return oauth2GrantType;
    }

    public String getOauth2Scope() {
        return oauth2Scope;
    }

    public String getOauth2ClientId() {
        return oauth2ClientId;
    }

    public String getOauth2ClientSecret() {
        return oauth2ClientSecret;
    }

    public boolean apiKeyIsConfigured() {
        return StringUtils.hasText(this.blindataUsername) && StringUtils.hasText(this.blindataPass) && StringUtils.hasText(blindataTenantUuid);
    }

    public String getOauth2ClientCertificate() {
        return oauth2ClientCertificate;
    }

    public String getOauth2ClientCertificatePrivateKey() {
        return oauth2ClientCertificatePrivateKey;
    }

    public boolean oauth2IsConfigured() {
        return StringUtils.hasText(this.oauth2Url) &&
                StringUtils.hasText(this.oauth2GrantType) &&
                StringUtils.hasText(this.oauth2Scope) &&
                StringUtils.hasText(this.oauth2ClientId) &&
                (StringUtils.hasText(this.oauth2ClientSecret) || (StringUtils.hasText(this.oauth2ClientCertificate) && StringUtils.hasText(this.oauth2ClientCertificatePrivateKey))) &&
                StringUtils.hasText(blindataTenantUuid);
    }

    public boolean getEnableAsync() {
        return enableAsync;
    }
}
