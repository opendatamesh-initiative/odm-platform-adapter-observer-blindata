# ODM Platform Blindata Observer Adapter

The Blindata Observer is the [Open Data Mesh Platform](https://github.com/opendatamesh-initiative) adapter that keeps a [Blindata](https://blindata.io/) catalog aligned with the platform. It registers itself as an observer in the Notification service and reacts to platform events: it uploads data products, ports and assets, quality checks and probes, stages and policy results to Blindata, and removes data products deleted in ODM. It handles both V1 events and the V2 events of ODM Platform V2 (Registry and DevOps 2.0). Optionally, it registers a validator in the Policy service that checks, before publication, that a data product can be uploaded to Blindata.

- **Source code:** [opendatamesh-initiative/odm-platform-adapter-observer-blindata](https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata)
- **Documentation:** [Configuration guide](https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata/blob/main/docs/configuration.md)
- **Issues:** [GitHub issues](https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata/issues)

## Tags

Each [GitHub release](https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata/releases) publishes one image tagged with its version, without the `v` prefix:

```bash
docker pull opendatamesh/odm-platform-adapter-observer-blindata:<version>
```

There is no `latest` tag: always pin a version. Tags before `2.0.0` are older releases of this adapter; this page describes the `2.x` images.

## Requirements

| Dependency | Required | Notes |
| --- | --- | --- |
| Blindata tenant | Yes | Authentication with a user and API key, or with OAuth2. See [Authentication](#authentication). |
| [Notification service](https://hub.docker.com/r/opendatamesh/odm-platform-notification) | Yes, to receive events | The observer registers itself on startup and receives events on `server.baseUrl`. |
| [Registry](https://hub.docker.com/r/opendatamesh/odm-platform-registry) | Depends on the events | Used to resolve port API definitions. Registry V2 (`apiVersion: v2`) is required for the DevOps 2.0 activity events. |
| Policy service | Only for policy results and the validator | Used by the `POLICIES_UPLOAD` use case, which reads policy evaluation results, and by the [validator](#validator). |

## Quick start

The image starts with the `docker` Spring profile, which reads the variables below. All of them must be set, even for disabled services: a missing one stops the startup with `Could not resolve placeholder`.

This example sets the Blindata credentials and disables Notification, Registry and Policy. Use it to check that the container starts: without Notification, the observer receives no events.

```bash
docker run --name odm-observer-blindata -p 9002:9002 \
  -e SPRING_PORT=9002 \
  -e SPRING_BASE_URL=http://localhost \
  -e BLINDATA_URL=https://blindata.example.com \
  -e BLINDATA_USER=observer@example.com \
  -e BLINDATA_PWD=change-me \
  -e BLINDATA_TENANT=00000000-0000-0000-0000-000000000000 \
  -e BLINDATA_ROLE=00000000-0000-0000-0000-000000000000 \
  -e NOTIFICATION_ACTIVE=false \
  -e NOTIFICATION_BASE_URL=http://notification.example.com \
  -e NOTIFICATION_PORT=8006 \
  -e REGISTRY_ACTIVE=false \
  -e REGISTRY_BASE_URL=http://registry.example.com \
  -e REGISTRY_PORT=8080 \
  -e POLICY_ACTIVE=false \
  -e POLICY_BASE_URL=http://policy.example.com \
  -e POLICY_PORT=8005 \
  opendatamesh/odm-platform-adapter-observer-blindata:<version>
```

When it's up, `http://localhost:9002/actuator/health` returns `{"status":"UP"}`.

## Production-like run with ODM Platform V2

Spring Boot reads nested configuration from the `SPRING_APPLICATION_JSON` environment variable, which takes precedence over the `docker` profile. This example enables Notification and Registry with their V2 APIs, sets a public callback URL, and replaces the default event handlers with handlers for V2 events:

```bash
docker run --name odm-observer-blindata -p 9002:9002 \
  -e SPRING_PORT=9002 \
  -e SPRING_BASE_URL=http://localhost \
  -e BLINDATA_URL=https://blindata.example.com \
  -e BLINDATA_USER=observer@example.com \
  -e BLINDATA_PWD=change-me \
  -e BLINDATA_TENANT=00000000-0000-0000-0000-000000000000 \
  -e BLINDATA_ROLE=00000000-0000-0000-0000-000000000000 \
  -e NOTIFICATION_ACTIVE=true \
  -e NOTIFICATION_BASE_URL=http://notification.example.com \
  -e NOTIFICATION_PORT=8006 \
  -e REGISTRY_ACTIVE=true \
  -e REGISTRY_BASE_URL=http://registry.example.com \
  -e REGISTRY_PORT=8080 \
  -e POLICY_ACTIVE=false \
  -e POLICY_BASE_URL=http://policy.example.com \
  -e POLICY_PORT=8005 \
  -e SPRING_APPLICATION_JSON='{
    "server": { "baseUrl": "https://observer-blindata.example.com" },
    "odm": { "productPlane": {
      "notificationService": { "apiVersion": "v2" },
      "registryService": { "apiVersion": "v2" }
    } },
    "blindata": { "eventHandlers": [
      { "eventType": "DATA_PRODUCT_INITIALIZED", "filter": "", "activeUseCases": ["DATA_PRODUCT_UPLOAD"] },
      { "eventType": "DATA_PRODUCT_VERSION_PUBLISHED", "filter": "", "activeUseCases": ["DATA_PRODUCT_UPLOAD", "DATA_PRODUCT_VERSION_UPLOAD", "QUALITY_UPLOAD", "PROBES_UPLOAD", "STAGES_UPLOAD"] },
      { "eventType": "DATA_PRODUCT_DELETED", "filter": "", "activeUseCases": ["DATA_PRODUCT_REMOVAL"] },
      { "eventType": "ACTIVITY_SUCCEEDED", "filter": "", "activeUseCases": ["STAGES_UPLOAD"] }
    ] }
  }' \
  opendatamesh/odm-platform-adapter-observer-blindata:<version>
```

- `server.baseUrl` must be a URL the Notification service can reach: it's where the observer receives events, on `/api/v1/up/observer/notifications` and `/api/v2/up/observer/notifications`. The `docker` profile sets it to `SPRING_BASE_URL:SPRING_PORT`; set it in `SPRING_APPLICATION_JSON` when the public URL is different, for example behind a proxy.
- With Notification `apiVersion: v2`, the observer subscribes twice: as `BLINDATA` for the events that only exist in V1 (activities, policies, marketplace, `DATA_PRODUCT_UPDATED`) and as `BLINDATA-V2` for V2 events. `DATA_PRODUCT_CREATED` and `DATA_PRODUCT_VERSION_CREATED` are not subscribed then, so the default handlers for them never run: configure handlers for the V2 event types, as above.
- An `eventHandlers` list in `SPRING_APPLICATION_JSON` replaces the whole default list.
- The DevOps 2.0 events (`ACTIVITY_SUCCEEDED`, `ACTIVITY_FAILED`) need Registry `active: true` and `apiVersion: v2`: the observer reads the descriptor from Registry V2.

Event types, use cases and filters are described in [Event handling](https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata/blob/main/docs/configuration/event-handling.md).

## Authentication

The observer authenticates to Blindata in one of two ways:

- **User and API key:** set `blindata.user`, `blindata.password` (the API key) and `blindata.tenantUUID` (`BLINDATA_USER`, `BLINDATA_PWD` and `BLINDATA_TENANT` with the `docker` profile).
- **OAuth2:** set `blindata.oauth2.url`, `grantType`, `scope`, `clientId`, and either `clientSecret` or `clientCertificate` with `clientCertificatePrivateKey` (PEM). `blindata.tenantUUID` is still required. With the `docker` profile, set `BLINDATA_USER` and `BLINDATA_PWD` to empty values and pass the `oauth2` block in `SPRING_APPLICATION_JSON`:

```json
{ "blindata": { "oauth2": {
  "url": "https://login.example.com/oauth2/v2.0/token",
  "grantType": "client_credentials",
  "scope": "https://blindata.example.com/.default",
  "clientId": "change-me",
  "clientSecret": "change-me"
} } }
```

Details: [Authentication](https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata/blob/main/docs/configuration/authentication.md).

## Validator

When `blindata.validator.active` is `true`, the observer registers on startup a policy engine and a policy in the Policy service (Policy V1 API). The policy is evaluated on data product and data product version creation and checks that the descriptor can be uploaded to Blindata; with `blocking: true`, a failing descriptor is not published. The Policy service calls the observer on `server.baseUrl` (`/api/v1/up/validator/evaluate-policy`), so the Policy service must be active and able to reach that URL.

Details: [Validator](https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata/blob/main/docs/configuration/validator.md) and [Validator error codes](https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata/blob/main/docs/validator-error-codes.md).

## Environment variables

### Image

| Variable | Description | Default |
| --- | --- | --- |
| `SPRING_PORT` | HTTP port (`server.port`). | none, required |
| `SPRING_BASE_URL` | Scheme and host of the observer, without port. The `docker` profile sets `server.baseUrl` to `SPRING_BASE_URL:SPRING_PORT`. | none, required |
| `BLINDATA_URL` | Base URL of the Blindata instance (`blindata.url`). | none, required |
| `BLINDATA_USER` | Blindata user for API key authentication (`blindata.user`). Empty with OAuth2. | none, required |
| `BLINDATA_PWD` | Blindata API key (`blindata.password`). Empty with OAuth2. | none, required |
| `BLINDATA_TENANT` | Blindata tenant UUID (`blindata.tenantUUID`). | none, required |
| `BLINDATA_ROLE` | UUID of the Blindata stewardship role assigned to the data product owner (`blindata.roleUuid`). | none, required |
| `NOTIFICATION_ACTIVE` | Register in the Notification service and receive events (`odm.productPlane.notificationService.active`). | none, required |
| `NOTIFICATION_BASE_URL`, `NOTIFICATION_PORT` | Notification service address, as `NOTIFICATION_BASE_URL:NOTIFICATION_PORT`. | none, required |
| `REGISTRY_ACTIVE` | Enable the Registry client (`odm.productPlane.registryService.active`). | none, required |
| `REGISTRY_BASE_URL`, `REGISTRY_PORT` | Registry address, as `REGISTRY_BASE_URL:REGISTRY_PORT`. | none, required |
| `POLICY_ACTIVE` | Enable the Policy service client (`odm.productPlane.policyService.active`). | none, required |
| `POLICY_BASE_URL`, `POLICY_PORT` | Policy service address, as `POLICY_BASE_URL:POLICY_PORT`. | none, required |
| `PROFILES_ACTIVE` | Spring profiles to activate. The `docker` profile maps all the variables above. | `docker` |
| `JAVA_OPTS` | Extra JVM options, for example `-Xmx512m`. | empty |
| `SPRING_PROPS` | Extra arguments added to the `java` command line before `-jar`, such as `-Dlogging.level.root=DEBUG`. It does not accept JSON: use `SPRING_APPLICATION_JSON` for that. | empty |

### Application

Every property can be set in `SPRING_APPLICATION_JSON`. The defaults are those of the image's `application.yml`; with the `docker` profile, the properties mapped to the variables above take their values from them.

| Property | Description | Default |
| --- | --- | --- |
| `server.port` | HTTP port. | `9002` |
| `server.baseUrl` | Externally reachable URL of the observer, used as the callback for the Notification and Policy services. | `http://localhost:9002` |
| `blindata.url` | Base URL of the Blindata instance. | none, required |
| `blindata.user` | User for API key authentication. | empty |
| `blindata.password` | API key for API key authentication. | empty |
| `blindata.tenantUUID` | Blindata tenant UUID. | none, required |
| `blindata.roleUuid` | Stewardship role assigned to the data product owner. | none, required |
| `blindata.oauth2.*` | OAuth2 client settings: `url`, `grantType`, `scope`, `clientId`, `clientSecret`, `clientCertificate`, `clientCertificatePrivateKey`. See [Authentication](#authentication). | empty |
| `blindata.enableAsync` | Use the Blindata asynchronous endpoints, for large descriptors that would time out. | `false` |
| `blindata.systemNameRegex` | Regex whose entire match on a port's `promises.platform` is the Blindata system name. | `.*` |
| `blindata.systemTechnologyRegex` | Regex whose entire match on a port's `promises.platform` is the system technology. | `[^:]*` |
| `blindata.dependsOnSystemNameRegex` | Regex applied to an input port's `dependsOn` / `x-dependsOn`; the first capture group is the system name. | `blindata:systems:(.+)` |
| `blindata.dataProducts.assetsCleanup` | Remove deprecated port assets and disable quality checks no longer in the descriptor. | `false` |
| `blindata.dataProducts.additionalPropertiesRegex` | Regex that selects extension fields as additional properties; its capture group is the property name. | `\bx-([\S]+)` |
| `blindata.dataProducts.domainTeamInheritance` | Set the data product team from its Blindata domain: `OFF`, `IF_MISSING` or `ALWAYS`. Any other value fails startup. | `OFF` |
| `blindata.dataProducts.missingDomainBlocksPublication` | When inheritance is not `OFF`, a domain that can't be found fails the validator. | `false` |
| `blindata.issueManagement.policies.active` | Active state of the issue policies uploaded to Blindata. | `true` |
| `blindata.probesUpload.connectionNamePropertyKey` | Port property that holds the Blindata Agent connection name, used by `PROBES_UPLOAD`. | `x-blindataConnectionName` |
| `blindata.eventHandlers` | List of `{eventType, filter, activeUseCases}`. `filter` is an optional SpEL expression on the event. | V1 handlers for `DATA_PRODUCT_CREATED`, `DATA_PRODUCT_VERSION_CREATED` and `DATA_PRODUCT_ACTIVITY_COMPLETED` |
| `blindata.validator.active` | Register the validator policy in the Policy service. | `false` |
| `blindata.validator.policyEngine.name` | Name of the validator policy engine. | `blindata_observer_validator` |
| `blindata.validator.policy.name` | Name of the validator policy. | `blindata_observer_validator_policy` |
| `blindata.validator.policy.blocking` | Whether a failed validation blocks publication. | `true` |
| `odm.productPlane.notificationService.active` / `.address` | Notification service client. | none, required |
| `odm.productPlane.notificationService.subscribeWithName` | Observer name in Notification. With `apiVersion: v2`, the V2 observer is named with a `-V2` suffix. | `BLINDATA` |
| `odm.productPlane.notificationService.apiVersion` | `v1` or `v2`. `v2` uses the Notification V2 API and also subscribes to V2 events. With any other value, no observer is registered. | `v1` |
| `odm.productPlane.registryService.active` / `.address` | Registry client. | none, required |
| `odm.productPlane.registryService.apiVersion` | `v1` or `v2`. `v2` enables the Registry V2 client, required for the DevOps 2.0 events. Any other value fails startup. | `v1` |
| `odm.productPlane.policyService.active` / `.address` | Policy service client (Policy V1 API). | none, required |

The full reference is in the [Configuration guide](https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata/blob/main/docs/configuration.md).

## Ports and volumes

- **Port:** `9002` (HTTP, exposed by the image), or the value of `SPRING_PORT`.
- **Health:** `/actuator/health`.
- **Volumes:** none. The observer is stateless.

## Learn more

- [Event handling](https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata/blob/main/docs/configuration/event-handling.md) and [DevOps 2.0 events](https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata/blob/main/docs/configuration/event-handling.md#devops-20-events)
- [Blindata configurations](https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata/blob/main/docs/configuration/blindata-configurations.md)
- [ODM Platform configurations](https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata/blob/main/docs/configuration/odm-platform-configurations.md)
- [Metadata mapping](https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata/blob/main/docs/mapping.md) and [Descriptor validation](https://github.com/opendatamesh-initiative/odm-platform-adapter-observer-blindata/blob/main/docs/validation.md)
- [Blindata documentation](https://help.blindata.io/)

Licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).
