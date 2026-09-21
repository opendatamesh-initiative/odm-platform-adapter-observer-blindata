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

`blindata.systemNameRegex` and `blindata.systemTechnologyRegex` both run on each port's `promises.platform` string.
They do not read the API schema. Each property contributes its entire match; a capturing group is not used. See
[Promises.Platform](../mapping.md#promisesplatform) for the mapping table.

Recommended `promises.platform` shape is a single `Technology:SystemName` value, for example `Snowflake:SALES_DW`.
The defaults in `application.yml` are `systemNameRegex: ".*"` and `systemTechnologyRegex: "[^:]*"`. With those
defaults the same value becomes technology `Snowflake` and name `Snowflake:SALES_DW` (the whole string). To store the
suffix as the system name, override the name regex so its entire match is that suffix:

```yaml
blindata:
  systemNameRegex: "(?<=:).*"    # Full match on promises.platform → system.name (SALES_DW)
  systemTechnologyRegex: "[^:]*" # Default. Full match on promises.platform → system.technology (Snowflake)
```

**Purpose**: Identify the Blindata system name and technology for the physical assets of a port.

**Examples** for `promises.platform` = `Snowflake:SALES_DW`:

- Default `[^:]*` → `system.technology` = `Snowflake`
- Default `.*` → `system.name` = `Snowflake:SALES_DW`
- `(?<=:).*` → `system.name` = `SALES_DW`

Do not use a pattern such as `system:(.+)` expecting only the capture group. The observer keeps the whole match
(`system:SALES_DW`). `dependsOnSystemNameRegex` is a different key and is not applied here.

### Port System Dependency Mapping

Configure how an input port's `dependsOn` or `x-dependsOn` value is resolved. This key is not applied to
`promises.platform`.

```yaml
blindata:
  dependsOnSystemNameRegex: "blindata:systems:(.+)"  # dependsOn / x-dependsOn only; first capture group
```

**Purpose**: When the dependency string matches, the first capturing group (or the entire match if there is no group)
is the Blindata system name for `port.dependsOnSystem`. A value that does not match is stored as
`port.dependsOnIdentifier` instead.

**Example**: `blindata:systems:SALES_DW` with the default `blindata:systems:(.+)` → system name `SALES_DW`.

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

## Quality Probes Upload

Configure how the `PROBES_UPLOAD` use case resolves Blindata Agent connection names from data product ports:

```yaml
blindata:
  probesUpload:
    connectionNamePropertyKey: x-blindataConnectionName  # Default
```

**Purpose**: When `PROBES_UPLOAD` is active on an event handler (included by default for `DATA_PRODUCT_VERSION_CREATED`, same pattern as `QUALITY_UPLOAD`), library/sql ODCS quality rules are materialized as Blindata CONTRACT_RULE probes **only on ports that declare a Blindata probe connection name**. Presence of that property is the per-port opt-in; omitting it skips probe upload for that port's library/sql rules (KQIs via `QUALITY_UPLOAD` are unaffected) and does not block publish. Agent connection setup and scheduling remain manual in Blindata. Remove `PROBES_UPLOAD` from `activeUseCases` to disable upload and the related validator checks.

**Notes**:

- Only `library` and `sql` contract rules become probes; legacy `scoreStrategy` rules are skipped.
- Declared connection names that are unknown to Blindata, or connections without a type, fail validation (validator dry-run) and block publish; no partial probe writes occur.
- Missing or blank connection names are treated as opt-out: those library/sql rules are skipped with an info log and do not fail validation.
- The validator enforces connection existence/type only when `PROBES_UPLOAD` is listed in the active use cases of at least one event handler and the port opts in with a connection name.
- Probe project name is stable and follows the Quality Suite code convention (`{domain} - {name}`); mutable
  `displayName` is not used.

## Configuration Parameters Reference

| Parameter                                | Type    | Default                 | Required | Description                        |
|------------------------------------------|---------|-------------------------|----------|------------------------------------|
| `roleUuid`                               | String  | -                       | No       | Role identifier for stewardship    |
| `systemNameRegex`                        | String  | `.*`                    | No       | Full match on `promises.platform` for `system.name` |
| `systemTechnologyRegex`                  | String  | `[^:]*`                 | No       | Full match on `promises.platform` for `system.technology` |
| `dependsOnSystemNameRegex`               | String  | `blindata:systems:(.+)` | No       | `dependsOn` / `x-dependsOn` only; first capture group |
| `enableAsync`                            | Boolean | `false`                 | No       | Enable async processing            |
| `dataProducts.assetsCleanup`             | Boolean | `true`                  | No       | Enable assets cleanup              |
| `dataProducts.additionalPropertiesRegex` | String  | `\\bx-([\\S]+)`         | No       | Regex for additional properties    |
| `issueManagement.policies.active`        | Boolean | `true`                  | No       | Enable issue policies              |
| `probesUpload.connectionNamePropertyKey` | String  | `x-blindataConnectionName` | No    | Port property key for probe connection name |

## Complete Configuration Example

```yaml
blindata:

  # promises.platform → system name / technology (entire match; not the API schema)
  systemNameRegex: "(?<=:).*"             # Snowflake:SALES_DW → system.name SALES_DW
  systemTechnologyRegex: "[^:]*"          # default; Snowflake:SALES_DW → system.technology Snowflake
  dependsOnSystemNameRegex: "blindata:systems:(.+)"  # dependsOn / x-dependsOn only; uses the capture group

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

- `systemNameRegex` and `systemTechnologyRegex` default to `.*` and `[^:]*` and both apply to `promises.platform`. The example above overrides only the name regex so `Snowflake:SALES_DW` becomes name `SALES_DW` and technology `Snowflake`.
- `dependsOnSystemNameRegex` applies to `dependsOn` / `x-dependsOn`, not to `promises.platform`.
- Performance settings should be adjusted based on your data product size and complexity
- Issue management settings control policy behavior across the platform 