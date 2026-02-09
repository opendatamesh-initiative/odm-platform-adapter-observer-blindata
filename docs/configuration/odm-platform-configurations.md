# ODM Platform Integration Configuration

Configure connections to Open Data Mesh microservices.

## Policy Service

```yaml
odm:
  productPlane:
    policyService:
      active: true
      address: The address of ODM Policy Service
```

## Registry Service

```yaml
odm:
  productPlane:
    registryService:
      active: true
      address: The address of ODM Registry Service
```

## Notification Service

```yaml
odm:
  productPlane:
    notificationService:
      active: true
      address: The address of ODM Notification Service
      subscribeWithName: "BLINDATA"  # Observer subscription name
      apiVersion: v1 OR v2
```

## Configuration Parameters

| Service              | Parameter           | Type    | Default | Description                                                                  |
|----------------------|---------------------|---------|---------|------------------------------------------------------------------------------|
| Policy Service       | `active`            | boolean | false   | Enable/disable policy service connection                                     |
|                      | `address`           | string  | -       | Policy service endpoint URL                                                  |
| Registry Service     | `active`            | boolean | false   | Enable/disable registry service connection                                   |
|                      | `address`           | string  | -       | Registry service endpoint URL                                                |
| Notification Service | `active`            | boolean | false   | Enable/disable notification service connection                               |
|                      | `address`           | string  | -       | Notification service endpoint URL                                            |
|                      | `subscribeWithName` | string  | -       | The name used by the observer when it subscribes to the notification service |
|                      | `apiVersion`        | string  | v1      | The version of the notification service api. When set to `v2`, the adapter subscribes to both V1 and V2 events (the V2 observer name uses the suffix `-V2`); see [Event Handling Configuration](event-handling.md) for supported events and use cases. |

## Service Roles

### Policy Service

The policy service integration enables:

- **Policy Evaluation**: Validating data products against governance policies
- **Policy Management**: Creating and managing data governance policies

### Registry Service

The registry service integration provides:

- **Data Product Metadata**: Access to data product descriptors and metadata
- **Version Management**: Tracking data product versions and changes
- **Asset Information**: Retrieving asset definitions and specifications

### Notification Service

The notification service integration handles:

- **Event Subscription**: Subscribing to Open Data Mesh platform events
- **Event Processing**: Receiving and processing platform notifications
