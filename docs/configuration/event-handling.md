# Event Handling Configuration

The observer adapter reacts to Notification Events emitted by the Open Data Mesh Platform. Configure event
subscriptions with two levels of granularity:

- **Event Types**:
  Supported event types:
    - `DATA_PRODUCT_CREATED`
    - `DATA_PRODUCT_UPDATED`
    - `DATA_PRODUCT_DELETED`
    - `DATA_PRODUCT_VERSION_CREATED`
    - `DATA_PRODUCT_VERSION_DELETED`
    - `DATA_PRODUCT_ACTIVITY_CREATED`
    - `DATA_PRODUCT_ACTIVITY_STARTED`
    - `DATA_PRODUCT_ACTIVITY_COMPLETED`
    - `DATA_PRODUCT_TASK_CREATED`
    - `DATA_PRODUCT_TASK_STARTED`
    - `DATA_PRODUCT_TASK_COMPLETED`
    - `POLICY_CREATED`
    - `POLICY_UPDATED`
    - `POLICY_DELETED`
    - `MARKETPLACE_EXECUTOR_RESULT_RECEIVED`

- **Event Content Filtering**

  Use SpEL expressions to filter events by content.
  E.g. An expression to capture an event regarding a data product ( match done by its fullyQualifiedName ).

  ```yaml
  blindata:
    eventHandlers: |
      [
        {
          "eventType": "DATA_PRODUCT_VERSION_CREATED",
          "filter": "#root['afterState']['info']['fullyQualifiedName'] == 'urn:org.opendatamesh:dataproducts:hrs:users'",
          "activeUseCases": [
            "DATA_PRODUCT_UPLOAD",
            "DATA_PRODUCT_VERSION_UPLOAD",
            "STAGES_UPLOAD",
            "POLICIES_UPLOAD"
          ]
        }
      ]
  ```

**Note:** _It is always mandatory to specify the event type._

## Available Actions

Configure the actions the observer will perform for each event:

For every subscription, it is possible to specify a set of actions that the observer will perform. These are:

| Action                                    | Description                                                                                                                 | Supported Event Types                                             |
|-------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------|
| `DATA_PRODUCT_UPLOAD`                     | Uploads the main information of the data product to Blindata and assigns ownership responsibilities to the designated user. When updating an existing data product, the version number is **not** updated and is preserved from the existing Blindata product. | `DATA_PRODUCT_CREATED`, `DATA_PRODUCT_VERSION_CREATED`            |
| `DATA_PRODUCT_VERSION_UPLOAD`             | Uploads the data product's ports metadata along with the assets specified in the descriptor API definitions. Also updates the data product version number in Blindata from the ODM descriptor. | `DATA_PRODUCT_VERSION_CREATED`, `DATA_PRODUCT_ACTIVITY_COMPLETED` |
| `QUALITY_UPLOAD`                          | Uploads the quality checks specified in the data product's ports metadata.                                                  | `DATA_PRODUCT_VERSION_CREATED`, `DATA_PRODUCT_ACTIVITY_COMPLETED` |
| `STAGES_UPLOAD`                           | Uploads the data product's stages which are defined inside the lifecycleInfo of the descriptor.                             | `DATA_PRODUCT_VERSION_CREATED`, `DATA_PRODUCT_ACTIVITY_COMPLETED` |
| `DATA_PRODUCT_REMOVAL`                    | Removes the **entire** data product from Blindata using the `dataProduct.fullyQualifiedName` from the event `beforeState`. | `DATA_PRODUCT_DELETED`                                            |
| `DATA_PRODUCT_VERSION_REMOVAL`            | Handles data product version deletion events as a **no-op** in Blindata: it logs that a version was deleted in ODM but does not create, update, or delete any Blindata data product. | `DATA_PRODUCT_VERSION_DELETED`                                    |
| `POLICIES_UPLOAD`                         | Gathers all policy evaluation results for the specified data product and uploads them to Blindata.                          | `DATA_PRODUCT_VERSION_CREATED`, `DATA_PRODUCT_ACTIVITY_COMPLETED` |
| `POLICIES_ALIGN`                          | Aligns Blindata Governance Policy Suites, Policies, and Policies Implementations using Odm Policy External Context field.   | `POLICY_CREATED`, `POLICY_UPDATED`, `POLICY_DELETED`              |
| `MARKETPLACE_ACCESS_REQUEST_PORTS_UPDATE` | Updates the grant status of the provider data product's ports in Blindata based on the result received from the platform.   | `MARKETPLACE_EXECUTOR_RESULT_RECEIVED`                            |

**Notes:**

- The version number handling differs between upload actions: `DATA_PRODUCT_UPLOAD` preserves the version when updating an existing product, while `DATA_PRODUCT_VERSION_UPLOAD` updates the version from the ODM descriptor.
- For deletion:
  - `DATA_PRODUCT_REMOVAL` uses only the `dataProduct` element of the event state and ignores any `dataProductVersion` field.
  - `DATA_PRODUCT_VERSION_REMOVAL` intentionally performs no Blindata-side mutation, ensuring that user-entered metadata on the Blindata data product is preserved when only a version is deleted in ODM.

## Examples

Below are comprehensive examples of event handler configurations for different scenarios:

### Example 1: Basic Data Product Lifecycle Management

This configuration handles the complete lifecycle of a data product:

```yaml
blindata:
  eventHandlers: |
    [
      {
        "eventType": "DATA_PRODUCT_VERSION_CREATED",
        "filter":"",
        "activeUseCases": [
          "DATA_PRODUCT_UPLOAD",     
          "DATA_PRODUCT_VERSION_UPLOAD",
          "QUALITY_UPLOAD",
          "STAGES_UPLOAD",
          "POLICIES_UPLOAD"
        ]
      },
      {
        "eventType": "DATA_PRODUCT_ACTIVITY_COMPLETED",
        "activeUseCases": [
          "STAGES_UPLOAD",
          "POLICIES_UPLOAD"
        ]
      }
    ]
```

### Example 2: Environment-Specific Deployment Strategy

This configuration employs a staged deployment strategy. Initially, basic data product information is uploaded upon the
creation of a version. Full details, however, are uploaded only after successful deployment. In this scenario, there are
two Blindata tenants: one for storing metadata from the development environment and another for the production
environment.

The configuration first uploads basic data product information when a version is created. Subsequently, it uploads
additional metadata, such as stages and policies, once activities are completed in the development environment. Finally,
it uploads comprehensive data product information, including ports and assets, when activities are completed in the
production environment.

**Note:** This configuration is intended for the production tenant. A separate observer with a distinct configuration
should be set up for the development tenant.

```yaml
blindata:
  eventHandlers: |
    [
      {
        "eventType": "DATA_PRODUCT_VERSION_CREATED",
        "activeUseCases": [
          "DATA_PRODUCT_UPLOAD"
        ]
      },
      {
        "eventType": "DATA_PRODUCT_ACTIVITY_COMPLETED",
        "filter": "#root['afterState']['activity']['stage'] == 'dev'",
        "activeUseCases": [
          "STAGES_UPLOAD",
          "POLICIES_UPLOAD"
        ]
      },
      {
        "eventType": "DATA_PRODUCT_ACTIVITY_COMPLETED",
        "filter": "#root['afterState']['activity']['stage'] == 'prod'",
        "activeUseCases": [
          "DATA_PRODUCT_UPLOAD",
          "DATA_PRODUCT_VERSION_UPLOAD",
          "STAGES_UPLOAD",
          "POLICIES_UPLOAD"
          ]
      }
    ]
```

### Example 3: Marketplace Integration

This configuration handles marketplace access request results:

```yaml
blindata:
  eventHandlers: |
    [
      {
        "eventType": "MARKETPLACE_EXECUTOR_RESULT_RECEIVED",
        "filter": "#root['afterState']['type'] == 'ACCESS_REQUEST_RESULT'",
        "activeUseCases": [
          "MARKETPLACE_ACCESS_REQUEST_PORTS_UPDATE"
        ]
      }
    ]
```
