# Authentication Configuration

The Blindata Observer supports multiple authentication methods to connect to the Blindata Platform.

## Basic Configuration

First, set the URL for communicating with Blindata:

```yaml
blindata:
  url: https://app.blindata.io # Url based on the Blindata instance
```

## API Key Authentication

Use API Key authentication for simple username/password authentication:

```yaml
blindata:
  url: https://app.blindata.io
  user: your-username
  password: your-password
  tenantUUID: your-tenant-uuid
```

## OAuth2 Authentication

For enterprise environments, use OAuth2 with external identity providers (Microsoft Entra ID, Google Cloud Identity,
etc.).

### Using Client Secret

```yaml
blindata:
  [ ... ]
  oauth2:
    url: https://login.microsoftonline.com/<microsoftTenantId>/oauth2/v2.0/token
    grantType: client_credentials
    scope: https://app.blindata.io/.default (an example)s
    clientId: <client identifier>
    clientSecret: <client secret>
  [ ... ]
```

### Using Certificate

For enhanced security, use certificate-based authentication.
The supported certificate format is .pem (Base64 format).

```yaml
blindata:
  [ ... ]
  oauth2:
    url: https://login.microsoftonline.com/<microsoftTenantId>/oauth2/v2.0/token
    grantType: client_credentials
    scope: https://app.blindata.io/.default
    clientId: <client identifier>
    clientCertificate: <client certificate with the public key>
    clientCertificatePrivateKey: <client certificate private key>
  [ ... ] 
```
