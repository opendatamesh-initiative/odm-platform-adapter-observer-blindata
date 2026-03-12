# Validator Error Codes

When the Blindata policy validator evaluates a data product, it logs warning messages for each validation issue. Every warning includes a **unique tag** at the beginning of the message in the format `[#<id>]` (e.g. `[#7]`, `[#100]`). This allows you to identify the error code and look up its meaning in this documentation.

## Tag format

- Every validator warning message starts with a tag: `[#<id>]`, where `<id>` is a numeric identifier.
- The tag is followed by a human-readable description (e.g. `[#100] Avro root schema must be a RECORD instead of STRING`).
- The validator returns all collected warnings in the policy evaluation result (`outputObject.rawError`), so clients and APIs can parse tags to categorize or document issues.

## Example entry

| Field | Content |
|-------|---------|
| **Tag** | `#100` |
| **Description** | Avro root schema must be a RECORD. |
| **Context** | Occurs when an Avro schema is provided for a payload definition (e.g. in an AsyncAPI port), but its root type is not a RECORD. |
| **Resolution** | Ensure the provided Avro schema defines a RECORD at its root level. |

---

## Tag reference by topic

*Placeholders like `%s` are filled at runtime with context-specific values (e.g. port name, FQN).*

### Data product and ODM info

| Tag | Description |
|-----|-------------|
| `[#9]` | %s Data product: %s has not been created yet on Blindata. |
| `[#10]` | %s Data product: %s has not policies evaluation results. |
| `[#12]` | %s Data Product with Fully Qualified Name: %s not found on Blindata. |
| `[#14]` | %s Data product: %s has not been created yet on Blindata. |
| `[#20]` | %s Data product: %s has not been created yet on Blindata. |
| `[#21]` | Impossible to retrieve Data Product version number from Odm Data Product Version. |
| `[#81]` | %s Missing odm data product info. |
| `[#82]` | %s Missing odm data product info fully qualified name. |
| `[#83]` | %s Missing odm data product info domain. |

### Port and standard definition

| Tag | Description |
|-----|-------------|
| `[#1]` | Quality extraction for data product port: %s with specification: %s and version: %s is not supported. |
| `[#3]` | Data product port: %s with specification: %s and version: %s is not supported. |
| `[#5]` | Failed to parse standard definition for port %s: %s. |
| `[#6]` | Missing specification on port. |
| `[#7]` | Missing specification version on port. |
| `[#8]` | Missing platform field on port. |

### Data product ports and assets

| Tag | Description |
|-----|-------------|
| `[#22]` | %s Missing interface components on data product: %s. |
| `[#23]` | Missing port identifier on data product port asset. |
| `[#24]` | Missing physical entities on data product port assets. |
| `[#25]` | Missing system on data product port assets. |
| `[#26]` | Missing name on data product port asset system. |
| `[#27]` | Missing name on data product port asset physical entity. |
| `[#28]` | Missing name on data product port asset physical field. |
| `[#29]` | Missing identifier on data product port. |
| `[#30]` | Missing name on data product port. |
| `[#31]` | Missing version on data product port. |
| `[#32]` | Missing type on data product port. |
| `[#33]` | Invalid regex for additional properties. |
| `[#34]` | %s: Both 'x-dependsOn' and 'dependsOn' are present. 'dependsOn' will be used. |
| `[#35]` | %s: System: %s not found in Blindata. |

### Policies upload and evaluation

| Tag | Description |
|-----|-------------|
| `[#11]` | %s Data product: %s error on policy result upload: %s. |

### Policy implementation and align

| Tag | Description |
|-----|-------------|
| `[#64]` | %s Invalid Policy Implementation built from Odm Policy: %s. |
| `[#65]` | %s External context policy is missing suite for Odm Policy: %s. |
| `[#66]` | %s External context policy is not valid for Odm Policy: %s. |
| `[#67]` | %s Error mapping external context to Blindata Policy Implementation: %s. |
| `[#68]` | %s Empty external context for Odm Policy: %s. |
| `[#70]` | %s Invalid Policy Implementation built from Odm Policy: %s. |
| `[#71]` | %s External context policy is missing suite for Odm Policy: %s. |
| `[#72]` | %s External context policy is not valid for Odm Policy: %s. |
| `[#73]` | %s Error mapping external context to Blindata Policy Implementation: %s. |
| `[#74]` | %s Empty external context for Odm Policy: %s. |

### Marketplace and access requests

| Tag | Description |
|-----|-------------|
| `[#15]` | %s Missing Access Request identifier. |
| `[#16]` | %s Marketplace Access Request: %s Missing Provider. |
| `[#17]` | %s Marketplace Access Request: %s Missing Provider ports. |
| `[#18]` | %s Access Request: %s error on port update result upload: %s. |

### Data product upload (responsibilities, contactPoints)

| Tag | Description |
|-----|-------------|
| `[#76]` | %s Impossible to assign responsibility on data product: %s, user: %s not found on Blindata. |
| `[#77]` | %s Impossible to assign responsibility on data product: %s, role: %s not found on Blindata. |
| `[#78]` | %s Product Type is not a textual value: %s. |
| `[#79]` | %s Failed to serialize contactPoints. |
| `[#80]` | %s Invalid regex for additional properties: %s. |

### Quality upload and checks

| Tag | Description |
|-----|-------------|
| `[#37]` | %s Quality Checks upload error: %s. |
| `[#38]` | %s Issue owner '%s' not found in Blindata for issue policy '%s'. |
| `[#39]` | %s Issue reporter '%s' not found in Blindata for issue policy '%s'. |
| `[#40]` | %s Data product owner '%s' not found in Blindata for data product: %s. |
| `[#41]` | %s Missing data product owner on data product: %s, skipping assignee on issue policies. |
| `[#42]` | %s Quality Check: %s is a reference and does not have a main declaration. |
| `[#43]` | %s Missing info fields on data product. |
| `[#44]` | %s Missing interface components on data product: %s. |

### Quality check validation (thresholds and strategies)

| Tag | Description |
|-----|-------------|
| `[#46]` | %s Quality Check validation failed: A valid code must be provided for the check. Quality Check: %s. |
| `[#47]` | %s Quality Check validation failed: A valid name must be provided for the check. Quality Check: %s. |
| `[#48]` | %s Quality Check validation failed: Thresholds must be defined. Quality Check: %s. |
| `[#49]` | %s Quality Check validation failed: Success threshold must be between 0 and 100 included. Quality Check: %s. |
| `[#50]` | %s Quality Check validation failed: Warning threshold must be between 0 and 100 included. Quality Check: %s. |
| `[#51]` | %s Quality Check validation failed: Warning threshold must be lower than or equal to the success threshold. Quality Check: %s. |
| `[#52]` | %s Quality Check validation failed: Unknown score strategy '%s'. Quality Check: %s. |
| `[#53]` | %s Quality Check validation failed: To properly calculate the score an expected value is mandatory for MINIMUM strategy. Quality Check: %s. |
| `[#54]` | %s Quality Check validation failed: To properly calculate the score a lowest acceptable value is mandatory for MINIMUM strategy. Quality Check: %s. |
| `[#55]` | %s Quality Check validation failed: The lowest acceptable value must be lower than or equal to the expected value for MINIMUM strategy. Quality Check: %s. |
| `[#56]` | %s Quality Check validation failed: To properly calculate the score an expected value is mandatory for MAXIMUM strategy. Quality Check: %s. |
| `[#57]` | %s Quality Check validation failed: To properly calculate the score a highest acceptable value is mandatory for MAXIMUM strategy. Quality Check: %s. |
| `[#58]` | %s Quality Check validation failed: The highest acceptable value must be greater than or equal to the expected value for MAXIMUM strategy. Quality Check: %s. |
| `[#59]` | %s Quality Check validation failed: To properly calculate the score an expected value is mandatory for DISTANCE strategy. Quality Check: %s. |
| `[#60]` | %s Quality Check validation failed: To properly calculate the score a lowest acceptable value is mandatory for DISTANCE strategy. Quality Check: %s. |
| `[#61]` | %s Quality Check validation failed: The lowest acceptable value must be lower than or equal to the expected value for DISTANCE strategy. Quality Check: %s. |
| `[#62]` | %s Quality Check validation failed: To properly calculate the score a highest acceptable value is mandatory for DISTANCE strategy. Quality Check: %s. |
| `[#63]` | %s Quality Check validation failed: The highest acceptable value must be greater than or equal to the expected value for DISTANCE strategy. Quality Check: %s. |

### Semantic linking

| Tag | Description |
|-----|-------------|
| `[#85]` | No default namespace identifier provided. |
| `[#86]` | Namespace not found for identifier. |
| `[#87]` | No default data category name provided. |
| `[#88]` | Data category not found in namespace. |
| `[#89]` | Data category not found. |
| `[#90]` | Unable to resolve semantic elements for semantic link path. |

### AsyncAPI and message schema

| Tag | Description |
|-----|-------------|
| `[#93]` | Missing schema format on message: %s, default AsyncApi Schema Object is not supported. |
| `[#96]` | Channel: %s, unsupported ref for AsyncApi port standard definition. |
| `[#97]` | Missing schema format on message: %s, default AsyncApi Schema Object is not supported. |

### Avro and JSON schema

| Tag | Description |
|-----|-------------|
| `[#100]` | Avro root schema must be a RECORD instead of %s. |
| `[#101]` | Avro schema %s of type %s is not supported. |
| `[#102]` | Schema %s of type %s is not supported. |

### Datastore API (tables, quality, schema)

| Tag | Description |
|-----|-------------|
| `[#103]` | Quality check does not have code. |
| `[#104]` | Quality check does not have code. |
| `[#105]` | Quality object inside datastoreApi is not valid. |
| `[#107]` | Unsupported issue policy type. |
| `[#108]` | Missing quality issue policy name for quality check. |
| `[#109]` | Missing quality issue policy type for quality check. |
| `[#110]` | Invalid regex for additional properties. |
| `[#111]` | Invalid regex for additional properties. |
| `[#112]` | Missing definition on datastore api table. |
| `[#113]` | Malformed definition on datastore api table. |
| `[#114]` | Data product port has empty schema, skipping quality checks extraction. |
| `[#115]` | Malformed port schema definition. |
| `[#117]` | Data product port has empty schema, skipping entities extraction. |
| `[#118]` | Malformed port schema definition. |

### Runtime exceptions (dynamic messages)

These tags indicate that the message content is provided by a thrown exception at runtime. The **Area** column gives a hint of where the error occurs.

| Tag | Area | Description |
|-----|------|-------------|
| `[#2]` | Port asset analysis (quality extraction) | Dynamic exception message. |
| `[#4]` | Port asset analysis (physical resources extraction) | Dynamic exception message. |
| `[#13]` | Data product removal | Dynamic exception message. |
| `[#19]` | Marketplace access request port update | Dynamic exception message. |
| `[#36]` | Data product ports and assets upload | Dynamic exception message. |
| `[#45]` | Quality upload | Dynamic exception message. |
| `[#69]` | Policy align (delete) | Dynamic exception message. |
| `[#75]` | Policy align | Dynamic exception message. |
| `[#84]` | Data product upload | Dynamic exception message. |
| `[#91]` | Semantic linking | Dynamic exception message. |
| `[#92]` | AsyncAPI v3 entities extraction | Dynamic exception message. |
| `[#94]` | AsyncAPI v3 entities extraction | Dynamic exception message. |
| `[#95]` | AsyncAPI v2 entities extraction | Dynamic exception message. |
| `[#98]` | AsyncAPI v2 entities extraction | Dynamic exception message. |
| `[#99]` | AsyncAPI message payload (JSON schema) | Dynamic exception message. |
| `[#106]` | Datastore API quality visitor | Dynamic exception message. |
| `[#116]` | Datastore API quality extraction | Dynamic exception message. |
| `[#119]` | Datastore API entities extraction | Dynamic exception message. |

---

## See also

- [Validator configuration](configuration/validator.md) – how to enable and configure the validator.
