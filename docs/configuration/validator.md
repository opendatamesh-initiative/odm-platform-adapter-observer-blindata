# Validator Configuration

The Observer includes a feature that automatically generates a Policy within the Policy Service, which is evaluated
before creating a DataProduct or DataProductVersion.

In the configuration, the policy can be set to blocking, ensuring that the data product is created only if it meets
compliance requirements. It verifies that the data product contains all necessary elements required by Blindata and can
be uploaded without issues.

## Basic Validator Configuration

```yaml
blindata:
  validator:
    active: true
    policyEngine:
      name: blindata-validator-engine
    policy:
      name: blindata-data-product-validation
      blocking: true  # Set to false for non-blocking validation
```

## Validator Parameters

| Parameter           | Type    | Default                            | Description                                     |
|---------------------|---------|------------------------------------|-------------------------------------------------|
| `active`            | boolean | false                              | Enable/disable automatic validation             |
| `policyEngine.name` | string  | blindata_observer_validator        | Name of the policy engine                       |
| `policy.name`       | string  | blindata_observer_validator_policy | Name of the generated policy                    |
| `policy.blocking`   | boolean | true                               | Whether validation blocks data product creation |

