# Blindata Configurations

<!-- TOC -->

* [Overview](#overview)
* [Metadata Extraction](#metadata-extraction)
    * [System Name and Technology Extraction](#system-name-and-technology-extraction)
    * [Port System Dependency Mapping](#port-system-dependency-mapping)
    * [Additional Properties Extraction](#additional-properties-extraction)
* [Data Product Management](#data-product-management)
    * [Assets Cleanup](#assets-cleanup)
    * [Stewardship Responsibilities](#stewardship-responsibilities)
* [Performance and Processing](#performance-and-processing)
    * [Asynchronous Processing](#asynchronous-processing)
* [Issue Management](#issue-management)
    * [Policy Management](#policy-management)
* [Configuration Parameters Reference](#configuration-parameters-reference)
* [Complete Configuration Example](#complete-configuration-example)

<!-- TOC -->

## Overview

The Blindata configuration section allows you to customize how the observer interacts with the Blindata platform. These
settings control metadata extraction, data product management, performance optimization, and issue management
capabilities.

## Metadata Extraction

### System Name and Technology Extraction

Configure regex patterns to extract system information from data product descriptors:

```yaml
blindata:
  systemNameRegex: "system:(.+)"           # Extract system name from schema
  systemTechnologyRegex: "tech:(.+)"       # Extract technology from schema
```

**Purpose**: These patterns allow the observer to automatically identify and extract system names and technologies from
data product descriptor API schemas, enabling proper system mapping in Blindata.

**Examples**:

- `system:(.+)` - Extracts system name after "system:" prefix
- `tech:(.+)` - Extracts technology information after "tech:" prefix

### Port System Dependency Mapping

Configure how port system dependencies are resolved:

```yaml
blindata:
  dependsOnSystemNameRegex: "blindata:systems:(.+)"  # Default regex for system dependencies
```

**Purpose**: This regex pattern helps identify and extract data product port system dependencies from the data product
descriptors, enabling proper dependency mapping in Blindata.

**Example**: `blindata:systems:(.+)` - Extracts system dependencies from Blindata-specific annotations

### Additional Properties Extraction

Configure how extension properties are extracted from data product descriptors:

```yaml
blindata:
  dataProducts:
    additionalPropertiesRegex: "\\bx-([\\S]+)"  # Extract extension properties
```

**Purpose**: A regex used to extract model extension fields as additional properties. The regex must contain a capture
group to define the name of the property.

**Example**: `^x-prop:(.*)` would turn a field like `x-prop:sourceTeam` into an additional property named `sourceTeam`.

## Data Product Management

### Assets Cleanup

Configure automatic cleanup of deprecated assets:

```yaml
blindata:
  dataProducts:
    assetsCleanup: true  # Default: true
```

**Purpose**: Enables or disables the cleanup of deprecated assets associated with data product ports. When enabled, any
quality checks defined in the Data Product Quality Suite but no longer listed in the quality section of the data product
descriptor will also be automatically disabled.

**Benefits**:

- Prevents accumulation of obsolete assets
- Maintains data quality consistency
- Reduces clutter in the Blindata interface

### Stewardship Responsibilities

Configure role-based stewardship assignment:

```yaml
blindata:
  roleUuid: "your-role-uuid"  # Optional role identifier
```

**Purpose**: This identifier is used to create or update responsibilities in Blindata. When provided, the observer can
assign stewardship responsibilities to data products based on the specified role.

**Note**: This parameter is optional and only required if you need to manage stewardship assignments.

## Performance and Processing

### Asynchronous Processing

Configure processing behavior for large data product descriptors:

```yaml
blindata:
  enableAsync: false  # Default: false
```

**Purpose**: When enabled, the observer will use the asynchronous endpoints of the Blindata API. This allows processing
of large data product descriptors without failing due to connection timeouts.

**When to enable**:

- Processing large data product descriptors
- Experiencing timeout issues
- Working with complex metadata structures

## Issue Management

### Policy Management

Configure how issue policies are handled:

```yaml
blindata:
  issueManagement:
    policies:
      active: true  # Default: true
```

**Purpose**: Controls the active state of issue policies uploaded to Blindata. When set to `false`, all issue policies
that are uploaded to Blindata are set to disabled.

**Use cases**:

- Temporarily disable all policies during maintenance
- Control policy activation based on environment
- Manage policy lifecycle

## Configuration Parameters Reference

| Parameter                                | Type    | Default                 | Required | Description                        |
|------------------------------------------|---------|-------------------------|----------|------------------------------------|
| `roleUuid`                               | String  | -                       | No       | Role identifier for stewardship    |
| `systemNameRegex`                        | String  | `.*`                    | No       | Regex to extract system name       |
| `systemTechnologyRegex`                  | String  | `[^:]*`                 | No       | Regex to extract system technology |
| `dependsOnSystemNameRegex`               | String  | `blindata:systems:(.+)` | No       | Regex for system dependencies      |
| `enableAsync`                            | Boolean | `false`                 | No       | Enable async processing            |
| `dataProducts.assetsCleanup`             | Boolean | `true`                  | No       | Enable assets cleanup              |
| `dataProducts.additionalPropertiesRegex` | String  | `\\bx-([\\S]+)`         | No       | Regex for additional properties    |
| `issueManagement.policies.active`        | Boolean | `true`                  | No       | Enable issue policies              |

## Complete Configuration Example

```yaml
blindata:

  # Metadata Extraction
  systemNameRegex: "system:(.+)"
  systemTechnologyRegex: "tech:(.+)"
  dependsOnSystemNameRegex: "blindata:systems:(.+)"

  # Performance
  enableAsync: false

  # Data Product Management
  dataProducts:
    assetsCleanup: true
    additionalPropertiesRegex: "\\bx-([\\S]+)"

  # Issue Management
  issueManagement:
    policies:
      active: true
```

**Notes**:

- Regex patterns are optional but recommended for proper metadata extraction
- Performance settings should be adjusted based on your data product size and complexity
- Issue management settings control policy behavior across the platform 