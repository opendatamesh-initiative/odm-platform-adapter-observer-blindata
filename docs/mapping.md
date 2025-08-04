# Mapping Data Product Descriptor into Blindata

This document describes how Open Data Mesh (ODM) data product descriptors are mapped into Blindata components, ensuring
seamless integration between descriptor structures and the Blindata environment.

<!-- TOC -->

* [Mapping Data Product Descriptor into Blindata](#mapping-data-product-descriptor-into-blindata)
    * [Overview](#overview)
    * [DataProductVersion.Info](#dataproductversioninfo)
    * [Ports](#ports)
        * [Input port dependency](#input-port-dependency)
    * [Promises.Platform](#promisesplatform)
    * [General Schema Annotations](#general-schema-annotations)
        * [Entities](#entities)
        * [Fields](#fields)
    * [Promises.Api.Definition: Specifications mapping in Blindata](#promisesapidefinition-specifications-mapping-in-blindata)
        * [Data Store Api](#data-store-api)
            * [From JSONSchema to Physical Entities](#from-jsonschema-to-physical-entities)
            * [From JSONSchema to Physical Field](#from-jsonschema-to-physical-field)
            * [From JSONSchema to Blindata Quality Checks](#from-jsonschema-to-blindata-quality-checks)
                * [Quality Suites](#quality-suites)
                * [Quality Checks](#quality-checks)
                * [Issue Policies](#issue-policies)
                * [Issue Campaigns](#issue-campaigns)
        * [AsyncApi](#asyncapi)
            * [From Avro to Physical Fields](#from-avro-to-physical-fields)
            * [From Json Schema to Physical Fields](#from-json-schema-to-physical-fields)
    * [Semantic Linking](#semantic-linking)
        * [Simple Example](#simple-example)
        * [Using Cross Relations](#using-cross-relations)
        * [Using Inverse Relations](#using-inverse-relations)
        * [Using Nested Relations](#using-nested-relations)
        * [Cross-Namespace Relations](#cross-namespace-relations)
        * [Complete Example: Customer Payment System](#complete-example-customer-payment-system)
* [Examples](#examples)
    * [Datastore Api Example](#datastore-api-example)
    * [Async Api](#async-api)
        * [Raw Port Async Api V3](#raw-port-async-api-v3)
        * [Raw Port Async Api V2](#raw-port-async-api-v2)
        * [Entities Async Api V3](#entities-async-api-v3)
        * [Entities Async Api V2](#entities-async-api-v2)

<!-- TOC -->

## Overview

When a data product descriptor is processed, the following Blindata components are created:

- **Data Products** (and their Ports)
- **Systems**
- **Physical Entities**
- **Physical Fields**

This mapping ensures that all data structures and their components are accurately represented and can be monitored or
managed within Blindata.

## DataProductVersion.Info

The `info` section of the data product descriptor is mapped into Blindata Data Product.

| Descriptor field                                   | Blindata field                     | Mandatory | Notes                                                                                                               |
|----------------------------------------------------|------------------------------------|-----------|---------------------------------------------------------------------------------------------------------------------|
| `info.name`                                        | `dataProduct.name`                 | ✔️        | If empty, we extract a name from                                                                                    |
| `info.displayName`                                 | `dataProduct.displayName`          | ️         | Defaults to `name` if not provided                                                                                  |
| `info.domain`                                      | `dataProduct.domain`               | ✔️        |                                                                                                                     |
| `info.fullyQualifiedName`                          | `dataProduct.identifier`           | ✔️        |                                                                                                                     |
| `info.version`                                     | `dataProduct.version`              | ️         | Defaults to `"0.0.0"` `productStatus="DRAFT"` if missing                                                            |
| `info.description`                                 | `dataProduct.description`          |           |                                                                                                                     |
| `info.x-productType`                               | `dataProduct.productType`          |           | Only textual values; others are logged and skipped                                                                  |
| `info._model extension properties matching regex_` | `dataProduct.additionalProperties` |           | Keys matching the configured regex on the application properties `blindata.dataProducts.additionalPropertiesRegex`. |

## Ports

Each entry in a `ports[]` array is mapped into a Blindata Data Product Port.

| Descriptor field                                                 | Blindata field                                       | Mandatory | Notes                                                                                                                     |
|------------------------------------------------------------------|------------------------------------------------------|-----------|---------------------------------------------------------------------------------------------------------------------------|
| `ports[].fullyQualifiedName`                                     | `port.identifier`                                    | ✔️        |                                                                                                                           |
| `ports[].name`                                                   | `port.name`                                          | ✔️        |                                                                                                                           |
| `ports[].displayName`                                            | `port.displayName`                                   |           |                                                                                                                           |
| `ports[].version`                                                | `port.version`                                       |           |                                                                                                                           |
| `ports[].description`                                            | `port.description`                                   |           |                                                                                                                           |
| `ports[].promises.servicesType`                                  | `port.servicesType`                                  |           |                                                                                                                           |
| `ports[].promises.slo.description`                               | additional property `sloDescription`                 |           | Extracted into `port.additionalProperties`                                                                                |
| `ports[].promises.deprecationPolicy.description`                 | additional property `deprecationPolicy`              |           | Extracted into `port.additionalProperties`                                                                                |
| _Port‐extension properties matching regex_                       | `port.additionalProperties`                          |           | Keys matching the configured regex (`blindata.dataProducts.additionalPropertiesRegex`) are extracted                      |
| `ports[].additionalProperties["dependsOn"]` or `["x-dependsOn"]` | `port.dependsOnSystem` or `port.dependsOnIdentifier` |           | Resolves to a system dependency if found, otherwise sets identifier directly. Prioritizes `dependsOn` over `x-dependsOn`. |

### Input port dependency

To map an external dependency for an InputPort, version 1.0.0 of the data product descriptor specification should be
extended with the ```x-dependsOn``` field. For versions > 1.0.0 it is possible to use the ```dependsOn``` field.

**Dependencies on Other Data Product Ports**
To define dependencies on another data product port, the `x-dependsOn` or `dependsOn` field must include a single string
representing
the fully qualified name (FQN) of the target data product port. This value will be uploaded to Blindata as the
dependsOnIdentifier.
It plays a critical role in resolving data product dependencies and reconstructing data lineage at the data product
level.

**Dependencies on Blindata Systems**
To map dependencies to systems in Blindata, the `x-dependsOn` or `dependsOn` field must contain a single string
comprising a prefix and the Blindata system name.
This format helps distinguish system dependencies from input port dependencies.
The application extracts the system name from the combined string using the regex specified in the
`blindata.dependsOnSystemNameRegex` property.
The default is `blindata:systems:(.+)`.

## Promises.Platform

The 'platform' field within the 'promises' field of the port in the descriptor is used to extract the name and system
technology of the system to be created in Blindata.

| Data product descriptor field | Blindata Field | Mandatory |
|-------------------------------|----------------|-----------|
| `promises.platform`           | `system.name`  | -         |

To extract the name and technology for association, two regular expressions must be defined within the Blindata
configurations, as shown below.

```yaml
blindata:
  systemNameRegex: optional regex to extract system name from schema (value optional)
  systemTechnologyRegex: optional regex to extract system technology from schema (value optional)
```

## General Schema Annotations

General schema annotations can be used to enrich schemas declarations with additional metadata information.
These annotations are independent form specifications and schema formats and are used to improve the understanding and
usability of
data structures.

### Entities

Entities annotations help to describe the general characteristics and metadata of a data structure. In the table below
there's a list of annotations each one have a detailed description, its requirement status, and support for JSONSchema
and Avro.

| Property        | Required | Description                                                                                                                              | JSONSchema Supported | Avro Supported |
|-----------------|----------|------------------------------------------------------------------------------------------------------------------------------------------|----------------------|----------------|
| `id`            | No       | The identifier of the schema item.                                                                                                       | -                    | -              |
| `name`          | No       | The name of the item.                                                                                                                    | ✔️                   | -              |
| `kind`          | No       | The entity structure archetype (e.g., tabular, event, etc.).                                                                             | ✔️                   | -              |
| `displayName`   | No       | The human-readable name of the item. It should be used by frontend tools to visualize the data item's name instead of the name property. | ✔️                   | -              |
| `summary`       | No       | A brief summary of the item.                                                                                                             | ✔️                   | -              |
| `description`   | No       | The item description. CommonMark syntax may be used for rich text representation.                                                        | ✔️                   | -              |
| `physicalType`  | No       | For entities: TABLE, VIEW, etc.                                                                                                          | ✔️                   | -              |
| `comments`      | No       | The comment annotation for adding comments to a schema. Its value must always be a string.                                               | ✔️                   | -              |
| `examples`      | No       | An array of examples for the item.                                                                                                       | ✔️                   | -              |
| `status`        | No       | The status of the item. Possible values include `testing`, `production`, or `staging`.                                                   | ✔️                   | -              |
| `tags`          | No       | A set of tags for categorizing the entity.                                                                                               | ✔️                   | -              |
| `externalDocs`  | No       | An array of links to reference external documentation.                                                                                   | ✔️                   | -              |
| `owner`         | No       | The owner of the entity schema. If the schema is not shared, it is the owner of the data product that defines the schema.                | ✔️                   | -              |
| `domain`        | No       | The domain to which the entity described by the schema belongs.                                                                          | ✔️                   | -              |
| `contactPoints` | No       | A collection of contact information for the given entity schema.                                                                         | ✔️                   | -              |
| `identifier`    | No       | An array of the entity's fields that compose its identifier (PK).                                                                        | ✔️                   | -              |
| `unique`        | No       | Indicates if the entity's identifier is unique.                                                                                          | ✔️                   | -              |

### Fields

Fields annotations help to describe the general characteristics and metadata of a data structure. In the table below
there's a list of annotations each one have a detailed description, its requirement status, and support for JSONSchema
and Avro.

| Property               | Required | Description                                                                                                                              | JSONSchema Supported | Avro Supported |
|------------------------|----------|------------------------------------------------------------------------------------------------------------------------------------------|----------------------|----------------|
| `id`                   | No       | The identifier of the schema item.                                                                                                       | -                    | -              |
| `name`                 | No       | The name of the item.                                                                                                                    | ✔️                   | -              |
| `kind`                 | No       | The field structure archetype (e.g., attribute, measure, etc.).                                                                          | ✔️                   | -              |
| `required`             | No       | Specifies if a field is required.                                                                                                        | ✔️                   | -              |
| `displayName`          | No       | The human-readable name of the item. It should be used by frontend tools to visualize the data item's name instead of the name property. | ✔️                   | -              |
| `summary`              | No       | A brief summary of the item.                                                                                                             | ✔️                   | -              |
| `description`          | No       | The item description. CommonMark syntax may be used for rich text representation.                                                        | ✔️                   | -              |
| `physicalType`         | No       | The physical type of the field (e.g., VARCHAR, TINYINT, etc.).                                                                           | ✔️                   | -              |
| `comments`             | No       | The comment annotation for adding comments to a schema. Its value must always be a string.                                               | ✔️                   | -              |
| `examples`             | No       | An array of examples for the item.                                                                                                       | ✔️                   | -              |
| `status`               | No       | The status of the item. Possible values include `testing`, `production`, or `staging`.                                                   | ✔️                   | -              |
| `tags`                 | No       | A set of tags for categorizing the field.                                                                                                | ✔️                   | -              |
| `externalDocs`         | No       | An array of links to reference external documentation.                                                                                   | ✔️                   | -              |
| `default`              | No       | The default value of the field, if any.                                                                                                  | ✔️                   | -              |
| `partitionStatus`      | No       | Indicates if the column is partitioned; possible values are true and false.                                                              | ✔️                   | -              |
| `partitionKeyPosition` | No       | The position of the partition column if the column is used for partitioning. Starts from 1.                                              | ✔️                   | -              |
| `clusterStatus`        | No       | Indicates if the column is clustered; possible values are true and false.                                                                | ✔️                   | -              |
| `clusterKeyPosition`   | No       | The position of the cluster column if the column is used for clustering.                                                                 | ✔️                   | -              |
| `readOnly`             | No       | Indicates that a value should not be modified.                                                                                           | ✔️                   | -              |
| `writeOnly`            | No       | Indicates that a value may be set, but will remain hidden.                                                                               | ✔️                   | -              |
| `isClassified`         | No       | Indicates if the field is classified.                                                                                                    | ✔️                   | -              |
| `classificationLevel`  | No       | The classification level of the field.                                                                                                   | ✔️                   | -              |
| `isUnique`             | No       | Indicates if the field value is unique.                                                                                                  | ✔️                   | -              |
| `isNullable`           | No       | Indicates if the field can be null.                                                                                                      | ✔️                   | -              |
| `pattern`              | No       | A regular expression to restrict a string field.                                                                                         | ✔️                   | -              |
| `format`               | No       | Allows for basic syntactic identification of certain kinds of string values (e.g., email, hostname, uuid, etc.).                         | ✔️                   | -              |
| `enum`                 | No       | Restricts the value of a field to a fixed set of values. It must be an array with at least one element, where each element is unique.    | ✔️                   | -              |
| `minLength`            | No       | Constrains the minimum length of a string field.                                                                                         | ✔️                   | -              |
| `maxLength`            | No       | Constrains the maximum length of a string field.                                                                                         | ✔️                   | -              |
| `contentEncoding`      | No       | The content encoding of the field.                                                                                                       | ✔️                   | -              |
| `contentMediaType`     | No       | The content media type of the field.                                                                                                     | ✔️                   | -              |
| `precision`            | No       | The precision of a numeric field.                                                                                                        | ✔️                   | -              |
| `scale`                | No       | The scale of a numeric field.                                                                                                            | ✔️                   | -              |
| `encoding`             | No       | The encoding type of the field.                                                                                                          | ✔️                   | -              |
| `minimum`              | No       | The minimum value of a numeric field.                                                                                                    | ✔️                   | -              |
| `exclusiveMinimum`     | No       | Specifies if the minimum value is exclusive.                                                                                             | ✔️                   | -              |
| `maximum`              | No       | The maximum value of a numeric field.                                                                                                    | ✔️                   | -              |
| `exclusiveMaximum`     | No       | Specifies if the maximum value is exclusive.                                                                                             | ✔️                   | -              |

## Promises.Api.Definition: Specifications mapping in Blindata

The observer supports the use of two specifications: `Datastore API` and `Async API`.
For Datastore API, the supported format for declaring entities is the `Standard Definition Object`.
For Async API the supported schema formats for the message payload are `AVRO` and `JSON SCHEMA`.

### Data Store Api

The Data Store API Specification (DSAS) defines a standard, language-agnostic interface to a Data API which allows both
humans and computers to understand how to establish a connection and query a database service managing tabular data
without access to source code, documentation, or through network traffic inspection. When properly defined, a consumer
can understand and interact with the remote database service with a minimal amount of implementation logic.

Other information about Data Store API is available
here: [Data Store API Specification](https://dpds.opendatamesh.org/specifications/dsas/1.0.0/)

To map tables and columns in Blindata use the schema field inside Data Store Api Entity, as shown in the example below:

```json
{
  "datastoreapi": ...,
  "info": ...,
  "services": ...,
  "schema": {
    "databaseName": "name of the database",
    "databaseSchemaName": "schema of the database",
    "tables": [
      {
        "id": "identifier",
        "name": "name",
        "version": "1.0.0",
        "description": "description",
        "specification": "json-schema",
        "specificationVersion": "1",
        "externalDocs": "docs",
        "definition": {
          Physical
          Entities
          Definition
        }
      }
    ]
  }
}
```

##### From JSONSchema to Physical Entities

This section describes the mapping of schema annotations to physical entity properties within the system. Physical
entities represent the main data structures, such as tables or views, in the data store.

Physical entities are mapped within the "definition" property inside the "tables" array in the "schema".

| Schema Annotation                           | Physical Entity Property | Description                                                         | Mandatory |
|---------------------------------------------|--------------------------|---------------------------------------------------------------------|-----------|
| `schema.tables[n].definition.name`          | name                     | The name of the physical entity.                                    | ✔️        |
| `schema.tables[n].definition.physicalType`  | tableType                | Specifies the type of the table (e.g., TABLE, VIEW).                | -         |
| `schematables[n].definition.description`    | description              | A detailed description of the physical entity.                      | -         |
| `schema.tables[n].definition.status`        | add.prop                 | The current status of the physical entity (e.g., active, inactive). | -         |
| `schema.tables[n].definition.tags`          | add.prop                 | Tags for categorizing the physical entity.                          | -         |
| `schema.tables[n].definition.domain`        | add.prop                 | The domain to which the physical entity belongs.                    | -         |
| `schema.tables[n].definition.contactPoints` | add.prop                 | Contact information for the physical entity.                        | -         |
| `schema.tables[n].definition.scope`         | add.prop                 | The scope of the physical entity within the system.                 | -         |
| `schema.tables[n].definition.externalDocs`  | add.prop                 | Links to external documentation related to the physical entity.     | -         |

#### From JSONSchema to Physical Field

This section details the mapping of schema annotations to physical field properties. Physical fields represent the
individual attributes or columns within a physical entity, providing specific data points within the entity.
Physical fields are mapped from "properties" field inside each entity definition.

| Schema Annotation                                            | Physical Field Property | Description                          | Mandatory |
|--------------------------------------------------------------|-------------------------|--------------------------------------|-----------|
| `schema.tables[n].definition.properties.<key>.name`          | name                    | Object name                          | ✔️        |
| `schema.tables[n].definition.properties.<key>.physicalType`  | type                    | Physical type of the object          | -         |
| `schema.tables[n].definition.properties.<key>.comments`      | description             | Additional comments about the object | -         |
| `schema.tables[n].definition.properties.<key>.kind`          | add.prop                | Object type (e.g., TABULAR)          | -         |
| `schema.tables[n].definition.properties.<key>.status`        | add.prop                | Object status (e.g., TESTING)        | -         |
| `schema.tables[n].definition.properties.<key>.tags`          | add.prop                | Tags associated with the object      | -         |
| `schema.tables[n].definition.properties.<key>.owner`         | add.prop                | Owner of the object                  | -         |
| `schema.tables[n].definition.properties.<key>.domain`        | add.prop                | Domain to which the object belongs   | -         |
| `schema.tables[n].definition.properties.<key>.contactpoints` | add.prop                | Contact points related to the object | -         |
| `schema.tables[n].definition.properties.<key>.scope`         | add.prop                | Scope of the object (e.g., private)  | -         |
| `schema.tables[n].definition.properties.<key>.version`       | add.prop                | Version of the object                | -         |
| `schema.tables[n].definition.properties.<key>.displayName`   | add.prop                | Human readable name of the object    | -         |
| `schema.tables[n].definition.properties.<key>.description`   | add.prop                | Description of the object            | -         |

#### From JSONSchema to Blindata Quality Checks

This section explains how schema annotations are mapped to Blindata Quality Checks.  
For more details about the Blindata Quality module, refer to
the [official documentation](https://help.blindata.io/data-quality/).

##### Quality Suites

When any quality annotation is present in the data product descriptor, a **Quality Suite** is automatically created with
the following values:

- **Quality Suite Code**: `<data product domain> - <data product name>`
- **Quality Suite Name**: `<data product domain> - <data product display name>`  
  *(If the display name is not provided, the data product name will be used instead.)*

All quality checks derived from the annotations are included in this Quality Suite.

##### Quality Checks

**Placement of Quality Annotations**

The `quality` property can be defined at two different levels within the schema:

- `schema.tables[n].definition.quality`  
  → The quality check will be associated with the corresponding **Physical Entity**.

- `schema.tables[n].definition.properties.<key>.quality`  
  → The quality check will be associated with the corresponding **Physical Field**.

**Structure of the `quality` Property**

The `quality` property is an array consisting of the following objects:

| Quality Object Property                            | Quality Check Property                | Description                                                                                                                                                                                                         | Mandatory |
|----------------------------------------------------|---------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------|
| `name`                                             | code, name                            | The quality check code will be a concatenation of the code of its Quality Suite and the vale of this property. KQI check name (uses `customProperties.displayName` if not specified)                                | ✔️        |
| `description`                                      | description                           | KQI description                                                                                                                                                                                                     |           |
| `dimension`                                        | additionalProperty: "dimension"       | If present, mapped into a Blindata Additional Property named "dimension", otherwise ignored                                                                                                                         |           |
| `unit`                                             | additionalProperty: "unit"            | If present, mapped into a Blindata Additional Property named "unit", otherwise ignored                                                                                                                              |           |
| `type`                                             | additionalProperty: "constraint_type" | If present, mapped into a Blindata Additional Property named "constraint_type", otherwise ignored                                                                                                                   |           |
| `engine`                                           | additionalProperty: "quality_engine"  | If present, mapped into a Blindata Additional Property named "quality_engine", otherwise ignored                                                                                                                    |           |
| `mustBeGreaterOrEqualTo`                           | scoreLeftValue                        | KQI Lowest Acceptable Value (used only for score strategies: MINIMUM, DISTANCE)                                                                                                                                     |           |
| `mustBeLessOrEqualTo`                              | scoreRightValue                       | KQI Highest Acceptable Value (used only for score strategies: MAXIMUM, DISTANCE)                                                                                                                                    |           |
| `mustBe`                                           | expectedValue                         | KQI Expected Value (used only for score strategies: MAXIMUM, MINIMUM, DISTANCE)                                                                                                                                     |           |
| `customProperties.displayName`                     | name                                  | KQI name                                                                                                                                                                                                            |           |
| `customProperties.scoreStrategy`                   | scoreStrategy                         | KQI score strategy                                                                                                                                                                                                  |           |
| `customProperties.scoreWarningThreshold`           | warningThreshold                      | KQI warning threshold                                                                                                                                                                                               |           |
| `customProperties.scoreSuccessThreshold`           | successThreshold                      | KQI success threshold                                                                                                                                                                                               |           |
| `customProperties.isCheckEnabled`                  | isEnabled (default true)              | KQI Is Check Enabled                                                                                                                                                                                                |           |
| `customProperties.blindataCustomProp-propertyName` | additionalProperty: "propertyName"    | If present, all the properties that starts with `blindataCustomProp-` are mapped into Blindata Additional Properties. E.g. `blindataCustomProp-IssueOwner` is mapped into an additional property named `IssueOwner` |           |
| `customProperties.refName`                         |                                       | Use this property to reference another quality definition code. The associated physical field or physical entity will then be included in the quality check.                                                        |           |

##### Issue Policies

This section explains how schema annotations are mapped to Blindata Issue Policies.  
For more details about the Blindata Issue Management module, refer to
the [official documentation](https://help.blindata.io/collaboration/issue-management/).

**Placement of Issue Policy Annotations**

The `issuePolicies` property can be defined under `[...].quality[n].customProperties.issuePolicies`.

**Structure of the `issuePolicies` Property**

The `issuePolicies` property is an array consisting of the following objects:

| Issue Policy Object Property      | Issue Policy Property                      | Description                                                                                                                                                                                     | Mandatory |
|-----------------------------------|--------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------|
| `name`                            | name                                       | The name of the Issue Policy                                                                                                                                                                    | ✔️        |
| `policyType`                      | policyType                                 | The type of the Issue Policy. Must be `SINGLE_RESULT_SEMAPHORE` of `RECURRENT_RESULT_SEMAPHORE`                                                                                                 | ✔️        |
| `semaphoreColor`                  | semaphores (default `RED`)                 | The color of the KQI semaphore that triggers the policy. Must be `RED` or `YELLOW` or `GREEN`                                                                                                   | ️         |
| `semaphoresNumber`                | policyContent.semaphoresNumber (default 1) | The number of semaphores after which a new Issue is generated by the Policy. Needed only for `RECURRENT_RESULT_SEMAPHORE`.                                                                      | ️         |
| `autoClose`                       | policyContent.autoClose (default `false`)  | If `true` the generated issue is closed if a result is detected with a semaphore different from the one selected by the semaphoreColor property.  Needed only for `RECURRENT_RESULT_SEMAPHORE`. | ️         |
| `severity`                        | issueTemplate.severity (default `INFO`)    | The severity of the generated Issue. Can be one of `BLOCKER`, `CRITICAL`, `MAJOR`, `MINOR`, `INFO`.                                                                                             | ️         |
| `blindataCustomProp-propertyName` | issueTemplate.additionalProperties         | All the properties that start with `blindataCustomProp-` will be the`additionalProperties` of the generated issue.                                                                              | ️         |
| -                                 | issueTemplate.priorityOrder                | The priority order of the generated Issue is set to `3` as default.                                                                                                                             | ️         |
| -                                 | issueTemplate.assignee                     | The default assignee of the generated Issue is taken from the data product owner, if present.                                                                                                   | ️         |

##### Issue Campaigns

An Issue Campaign is generated for each data product. All the issues generated by the automated Issue Policies will be
part of this Campaign.
The Campaign name is `Quality - <domainName> - <dataProductName>`.

### AsyncApi

The supported version are 2.x.x and 3.x.x.
Each `channel` declared inside the specification is mapped into a Physical Entity as follows.

| Specification field    | Physical Entity Property | Mandatory | Default Value |
|------------------------|--------------------------|-----------|---------------|
| `channel.name`         | name                     | ✔️        | -             |
| `channel`              | tableType                | -         | TOPIC         |
| `channel.description`  | description              | -         | -             |
| `channel.servers`      | add.prop                 | -         | -             |
| `channel.parameters`   | add.prop                 | -         | -             |
| `channel.tags`         | add.prop                 | -         | -             |
| `channel.externalDocs` | add.prop                 | -         | -             |
| `channel.summary`      | add.prop                 | -         | -             |
| `channel.address`      | add.prop                 | -         | -             |

Each `message` with its payload schema is mapped into a set of Physical Fields, each one associated to the
Physical Entity that represents the `channel`.

The `message` `payload` schema can be defined in different formats and encoding.
The supported ones are: `vnd.apache.avro` and `schema+json`.

##### From Avro to Physical Fields

When a `message` `payload` schema is defined in `vnd.apache.avro` format, the message with its content
is mapped as follows.

| Specification field                    | Physical Field Property |
|----------------------------------------|-------------------------|
| `[..].payload.schema.fields.[..].name` | name                    |
| `[..].payload.schema.fields.[..].type` | type                    |
| The position in the fields array       | ordinalPosition         |

##### From Json Schema to Physical Fields

When a `message` `payload` schema is defined in `schema+json` format, the message with its content
is mapped as follows.

The `message` is mapped in a Physical Field as follows.

| Specification field        | Physical Field Property |
|----------------------------|-------------------------|
| `[..].message.id`          | name                    |
| `[..].message.contentType` | type                    |

The `message` `payload` is mapped in a set of Physical Fields as follows.

| Specification field                   | Physical Field Property |
|---------------------------------------|-------------------------|
| `[..].payload.[..].<key>`             | name                    |
| `[..].payload.[..].<key>.type`        | type                    |
| `[..].payload.[..].<key>.javaType`    | type                    |
| `[..].payload.[..].<key>.description` | description             |
| `[..].payload.[..].<key>.required`    | add.prop                |

## Semantic Linking

A semantic link is an explicit connection between a data asset exposed by a data product through one of its output ports
and one or more concepts defined in a central enterprise ontology.

**Note**: Before starting, ensure that all the ontology elements (concepts, attributes and relationships) are correctly
represented in Blindata. This can also be validated by the Observer validator functionality.

### Simple Example

The simplest form of semantic linking connects a field directly to a concept attribute. This is useful when the field
name
in your data asset directly corresponds to an attribute in the ontology.

**Example: Direct Field to Concept Mapping**

```json
{
  "s-context": {
    "s-base": "https://demo.blindata.io/logical/namespaces/name/filmRentalInc#",
    "s-type": "[Movie]",
    "movie_id": null,
    "title": "[Movie].title",
    "duration": "[Movie].duration"
  }
}
```

In this example:

- `title` field maps directly to the `title` attribute of the `Movie` concept
- `duration` field maps directly to the `duration` attribute of the `Movie` concept
- `movie_id` is set to `null` indicating it's not part of the ontology (physical-only field)

### Using Cross Relations

Cross relations allow you to link fields to attributes of related concepts through relationship chains. This is useful
when
your data asset contains information about related entities.

**Example: Single Hop Relationship**

```json
{
  "s-context": {
    "s-base": "https://demo.blindata.io/logical/namespaces/name/filmRentalInc#",
    "s-type": "[Movie]",
    "director_name": "[Movie].director[Person].fullName",
    "director_email": "[Movie].director[Person].email"
  }
}
```

In this example:

- `director_name` field maps to the `fullName` attribute of the `Person` concept, accessed through the `director`
  relationship
- `director_email` field maps to the `email` attribute of the `Person` concept, also accessed through the `director`
  relationship

**Example: Multi-Hop Relationship Chain**

```json
{
  "s-context": {
    "s-base": "https://demo.blindata.io/logical/namespaces/name/filmRentalInc#",
    "s-type": "[Movie]",
    "director_country": "[Movie].director[Person].country[Country].name",
    "distributor_name": "[Company].distributes[Movie].name"
  }
}
```

In this example:

- `director_country` follows a two-hop chain: `Movie` → `director` → `Person` → `country` → `Country` → `name`
- `distributor_name` follows a reverse relationship: `Company` → `distributes` → `Movie` → `name`

### Using Inverse Relations

Inverse relations allow you to traverse relationships in the opposite direction using the `^` prefix. This is useful
when
you need to access information from the "from" side of a relationship.

**Example: Inverse Relationship Navigation**

```json
{
  "s-context": {
    "s-base": "https://demo.blindata.io/logical/namespaces/name/filmRentalInc#",
    "s-type": "[Movie]",
    "director_info": "[Movie].^director[Person].name",
    "actor_list": "[Movie].^actor[Person].name"
  }
}
```

In this example:

- `director_info` uses `^director` to find the person who directed this movie (inverse of Person → director → Movie)
- `actor_list` uses `^actor` to find all people who acted in this movie (inverse of Person → actor → Movie)

### Using Nested Relations

Nested relations allow you to define complex object structures that map to multiple related concepts. This is useful for
representing hierarchical or composite data structures.

**Example: Nested Object with Multiple Concepts**

```json
{
  "s-context": {
    "s-base": "https://demo.blindata.io/logical/namespaces/name/filmRentalInc#",
    "s-type": "[Movie]",
    "copyright": {
      "s-type": "copyrightHolder[Organization]",
      "organization_id": "vatNumber",
      "email": "contactPoint[ContactPoint].mail"
    },
    "soundtrack": {
      "s-type": "[MusicRecording]",
      "artist": "byArtist[MusicGroup].name",
      "duration": "duration"
    }
  }
}
```

In this example:

- The `copyright` object maps to an `Organization` concept with nested relationships
- The `soundtrack` object maps to a `MusicRecording` concept with its own set of relationships
- Each nested object can have its own `s-type` and relationship chains

### Cross-Namespace Relations

When working with multiple ontologies or namespaces, you can specify the namespace prefix to disambiguate between
concepts
with the same name in different namespaces.

**Example: Cross-Namespace Linking**

```json
{
  "s-context": {
    "s-base": "https://blindata.io#logisticsOntology#",
    "s-type": "[Stock]",
    "product_sku": "[Stock].refersTo[lux:ProductSku].lux:productSkuIdentifier",
    "product_name": "[Stock].refersTo[lux:ProductSku].lux:name"
  }
}
```

In this example:

- The `Stock` concept is in the logistics namespace
- The `ProductSku` concept is in the luxury namespace (prefixed with `lux:`)
- The relationship `refersTo` connects concepts across different namespaces

### Complete Example: Customer Payment System

Here's a comprehensive example showing how semantic linking can be used in a customer payment system:

```json
{
  "s-context": {
    "s-base": "https://demo.blindata.io/logical/namespaces/name/filmRentalInc#",
    "s-type": "[Customer]",
    "customer_id": "[Customer].Customer ID",
    "customer_email": "[Customer].Email",
    "payment_month": "[Customer].makes a[Payment].Payment Date",
    "total_payment": "[Customer].makes a[Payment].Amount",
    "payment_method": "[Customer].makes a[Payment].method[PaymentMethod].name",
    "payment_customer": "[Payment].^makes a[Customer].name",
    "billing_address": {
      "s-type": "billingAddress[Address]",
      "street": "street",
      "city": "city[City].name",
      "country": "city[City].country[Country].name"
    }
  }
}
```

This example demonstrates:

- Direct attribute mapping (`customer_id`, `customer_email`)
- Single-hop relationships (`payment_month`, `total_payment`)
- Multi-hop relationships (`payment_method`)
- Inverse relationships (`payment_customer` - finding the customer who made a payment)
- Nested objects with complex relationships (`billing_address`)
- Cross-concept navigation through relationship chains

# Examples

## Datastore Api Example

This section provides examples of how to represent multiple entities using the Data Store API. It demonstrates the
application of schema annotations to define multiple physical entities and their fields, illustrating how the mappings
are structured and implemented in practice.
From the descriptor the observer will create two physical entities called "Customer" and "Payments" each one with just a
physical field "id".

```json
{
  "datastoreapi": "1.0.0",
  "info": {
    "title": "flight_frequency Data",
    "summary": "This API exposes the current flight_frequency data of each `Airline` entity",
    "description": "API description",
    "termsOfService": "https://example.com/terms/",
    "version": "1.0.0",
    "datastoreName": "flight_frequency",
    "contact": {
      "name": "API Support",
      "url": "https://www.example.com/support",
      "email": "support@example.com"
    },
    "license": {
      "name": "Apache 2.0",
      "url": "https://www.apache.org/licenses/LICENSE-2.0.html"
    },
    "nameSpace": "nome_schema"
  },
  "services": {
    "development": {
      "name": "Flight frequency service",
      "description": "The service tablethat host the `FREQUENCY` data store in the given environment",
      "serverInfo": {
        "flightFrequencyStoreServerInfo": {
          "host:": "{host}",
          "port:": "3306",
          "dbmsType:": "MySQL",
          "dbmsVersion:": "8",
          "connectionProtocols": {
            "jdbc": {
              "version": "1.0",
              "url": "jdbc:mysql://{hosts}:3306/foodmart",
              "driverName": "MySQL JDBC Driver",
              "driverClass": "org.mysql.Driver",
              "driverVersion": "latest",
              "driverLibrary": {
                "description": "MySQL JDBC Driver Library",
                "dataType": "application/java-archive",
                "$href": "https://jdbc.mysql.org/"
              },
              "driverDocs": {
                "description": "MySQL JDBC Driver HomePage",
                "dataType": "text/html",
                "$href": "https://jdbc.mysql.org/mysql-8.jar"
              }
            }
          }
        }
      },
      "variables": {
        "host": "35.52.55.12"
      }
    }
  },
  "schema": {
    "databaseName": "flightdb",
    "databaseSchemaName": "dwh",
    "tables": [
      {
        "id": "identifier",
        "name": "name",
        "version": "1.0.0",
        "description": "description",
        "specification": "json-schema",
        "specificationVersion": "1",
        "externalDocs": "docs",
        "definition": {
          "name": "Customer",
          "type": "object",
          "properties": {
            "id": {
              "type": "string",
              "description": "The customer identifier.",
              "name": "ID",
              "kind": "ATTRIBUTE",
              "required": true,
              "displayName": "Identifier",
              "summary": "Inline description",
              "comments": "come sopra",
              "examples": [
                "1234567",
                "988654"
              ],
              "status": "come sopra",
              "tags": [
                "tag1",
                "tag2"
              ],
              "externalDocs": "https://www.google.it/",
              "default": null,
              "isClassified": true,
              "classificationLevel": "INTERNAL",
              "isUnique": true,
              "isNullable": false,
              "pattern": "^[0-9]+$",
              "format": "named pattern e.g. email",
              "enum": [
                "VAL1",
                "VAL2"
              ],
              "minLength": 2,
              "maxLength": 10,
              "contentEncoding": "UTF-8",
              "contentMediaType": "application/json",
              "precision": 0,
              "scale": 10,
              "minimum": 0,
              "exclusiveMinimum": true,
              "maximum": 10000,
              "exclusiveMaximum": false,
              "readOnly": true,
              "writeOnly": true,
              "physicalType": "VARCHAR",
              "partitionStatus": true,
              "partitionKeyPosition": 2,
              "clusterStatus": true,
              "clusterKeyPosition": 2,
              "ordinalPosition": 0
            }
          }
        }
      },
      {
        "id": "identifier",
        "name": "name",
        "version": "1.0.0",
        "description": "description",
        "specification": "json-schema",
        "specificationVersion": "1",
        "externalDocs": "docs",
        "definition": {
          "name": "Payment",
          "type": "object",
          "properties": {
            "id": {
              "type": "string",
              "description": "The customer identifier.",
              "name": "ID",
              "kind": "ATTRIBUTE",
              "required": true,
              "displayName": "Identifier",
              "summary": "Inline description",
              "comments": "come sopra",
              "examples": [
                "1234567",
                "988654"
              ],
              "status": "come sopra",
              "tags": [
                "tag1",
                "tag2"
              ],
              "externalDocs": "https://www.google.it/",
              "default": null,
              "isClassified": true,
              "classificationLevel": "INTERNAL",
              "isUnique": true,
              "isNullable": false,
              "pattern": "^[0-9]+$",
              "format": "named pattern e.g. email",
              "enum": [
                "VAL1",
                "VAL2"
              ],
              "minLength": 2,
              "maxLength": 10,
              "contentEncoding": "UTF-8",
              "contentMediaType": "application/json",
              "precision": 0,
              "scale": 10,
              "minimum": 0,
              "exclusiveMinimum": true,
              "maximum": 10000,
              "exclusiveMaximum": false,
              "readOnly": true,
              "writeOnly": true,
              "physicalType": "VARCHAR",
              "partitionStatus": true,
              "partitionKeyPosition": 2,
              "clusterStatus": true,
              "clusterKeyPosition": 2,
              "ordinalPosition": 0
            }
          }
        }
      }
    ]
  }
}
```

## Async Api

### Raw Port Async Api V3

This section illustrates an example of an Async API version 3 configuration for a Trip Status Streaming API. The API is
used to stream events related to the Trip entity. It describes the schema for the messages, including the fields and
their types.

```json
{
  "mediaType": "text/json",
  "asyncapi": "3.0.0",
  "info": {
    "title": "Trip Status Streaming API",
    "version": "1.0.0",
    "description": "This API exposes all events related to `Trip` entity"
  },
  "channels": {
    "transportmng.tripexecution.devents.status": {
      "messages": {
        "tripStatusEvent": {
          "payload": {
            "schemaFormat": "application/vnd.apache.avro;version=1.9.0",
            "schema": {
              "type": "record",
              "name": "TripStatusChange",
              "namespace": "com.company-xyz.transportmng.tripexecution",
              "fields": [
                {
                  "name": "id",
                  "type": {
                    "avro.java.string": "String",
                    "type": "string"
                  }
                },
                {
                  "name": "event_type",
                  "type": {
                    "name": "tripEvent",
                    "symbols": [
                      "planned",
                      "booking_started",
                      "booking_ended",
                      "loading_started",
                      "loading_ended",
                      "departed_from_origin",
                      "unloading_at_stop_started",
                      "unloading_at_stop_ended",
                      "loading_at_stop_started",
                      "loading_at_stop_ended",
                      "departed_from_stop",
                      "arrived_at_destination",
                      "unloading_started",
                      "unloading_ended",
                      "completed"
                    ],
                    "type": "enum"
                  }
                },
                {
                  "name": "event_timestamp",
                  "type": {
                    "avro.java.string": "String",
                    "type": "string"
                  }
                },
                {
                  "name": "source_system",
                  "default": "TMS",
                  "type": {
                    "avro.java.string": "String",
                    "type": "string"
                  }
                }
              ]
            }
          }
        }
      }
    }
  }
}

```

### Raw Port Async Api V2

This section provides an example of an Async API version 2 configuration for a Trip Status Streaming API. It includes
details on the servers, channels, and message schemas used for streaming events related to the Trip entity.

```json
{
  "mediaType": "text/json",
  "asyncapi": "2.5.0",
  "info": {
    "title": "Trip Status Streaming API",
    "version": "1.0.0",
    "description": "This API exposes all events related to `Trip` entity"
  },
  "servers": {
    "development": {
      "url": "https://company-xyz.com/platform/dev/confluent-cloud",
      "description": "Confluent Cloud DEV bootstrap server",
      "protocol": "kafka",
      "protocolVersion": "latest",
      "bindings": {
        "kafka": {
          "schemaRegistryUrl": "https://company-xyz.com/platform/dev/confluent-schema-registry",
          "schemaRegistryVendor": "confluent"
        }
      }
    },
    "production": {
      "url": "https://company-xyz.com/platform/prod/confluent-cloud",
      "description": "Confluent Cloud PRODUCTION bootstrap server",
      "protocol": "kafka",
      "protocolVersion": "latest",
      "bindings": {
        "kafka": {
          "schemaRegistryUrl": "https://company-xyz.com/platform/prod/confluent-schema-registry",
          "schemaRegistryVendor": "confluent"
        }
      }
    }
  },
  "defaultContentType": "avro/binary",
  "channels": {
    "transportmng.tripexecution.devents.status": {
      "description": "This topic contains all the *domain events* related to `Trip` entity",
      "subscribe": {
        "operationId": "readTripStatusEvents",
        "security": [
          {
            "apiKey": []
          }
        ],
        "message": {
          "messageId": "tripStatusEvent",
          "contentType": "avro/binary",
          "schemaFormat": "application/vnd.apache.avro",
          "payload": {
            "type": "record",
            "name": "TripStatusChange",
            "namespace": "com.company-xyz.transportmng.tripexecution",
            "fields": [
              {
                "name": "id",
                "type": {
                  "avro.java.string": "String",
                  "type": "string"
                }
              },
              {
                "name": "event_type",
                "type": {
                  "name": "tripEvent",
                  "symbols": [
                    "planned",
                    "booking_started",
                    "booking_ended",
                    "loading_started",
                    "loading_ended",
                    "departed_from_origin",
                    "unloading_at_stop_started",
                    "unloading_at_stop_ended",
                    "loading_at_stop_started",
                    "loading_at_stop_ended",
                    "departed_from_stop",
                    "arrived_at_destination",
                    "unloading_started",
                    "unloading_ended",
                    "completed"
                  ],
                  "type": "enum"
                }
              },
              {
                "name": "event_timestamp",
                "type": {
                  "avro.java.string": "String",
                  "type": "string"
                }
              },
              {
                "name": "source_system",
                "default": "TMS",
                "type": {
                  "avro.java.string": "String",
                  "type": "string"
                }
              }
            ]
          },
          "bindings": {
            "kafka": {
              "schemaIdPayloadEncoding": "confluent",
              "schemaLookupStrategy": "TopicIdStrategy",
              "key": {
                "type": "string",
                "enum": [
                  "id"
                ]
              }
            }
          }
        }
      },
      "bindings": {
        "kafka": {
          "partitions": 20,
          "replicas": 3
        }
      }
    },
    "transportmng.tripexecution.devents.position": {
      "description": "This topic contains all the *domain events* related position tracking of `Trip` entity",
      "subscribe": {
        "operationId": "readTripPositionEvents",
        "security": [
          {
            "apiKey": []
          }
        ],
        "message": {
          "messageId": "tripPositionEvent",
          "contentType": "avro/binary",
          "schemaFormat": "application/vnd.apache.avro",
          "payload": {
            "type": "record",
            "name": "TripPositionNotification",
            "namespace": "com.company-xyz.transportmng.tripexecution",
            "fields": [
              {
                "name": "id",
                "type": {
                  "avro.java.string": "String",
                  "type": "string"
                }
              },
              {
                "name": "position",
                "type": {
                  "avro.java.string": "String",
                  "type": "string"
                }
              },
              {
                "name": "event_timestamp",
                "type": {
                  "avro.java.string": "String",
                  "type": "string"
                }
              }
            ]
          },
          "bindings": {
            "kafka": {
              "schemaIdPayloadEncoding": "confluent",
              "schemaLookupStrategy": "TopicIdStrategy",
              "key": {
                "type": "string",
                "enum": [
                  "id"
                ]
              }
            }
          }
        }
      },
      "bindings": {
        "kafka": {
          "partitions": 20,
          "replicas": 3
        }
      }
    }
  },
  "components": {
    "securitySchemes": {
      "apiKey": {
        "type": "apiKey",
        "in": "user"
      }
    }
  }
}

```

### Entities Async Api V3

```json
{
  "physicalEntities": [
    {
      "uuid": null,
      "schema": null,
      "name": "transportmng.tripexecution.devents.status",
      "description": null,
      "dataSet": null,
      "creationDate": null,
      "modificationDate": null,
      "physicalFields": [
        {
          "uuid": null,
          "name": "tripStatusEvent.id",
          "type": "string",
          "ordinalPosition": 1,
          "description": null,
          "creationDate": null,
          "modificationDate": null,
          "additionalProperties": []
        },
        {
          "uuid": null,
          "name": "tripStatusEvent.event_timestamp",
          "type": "string",
          "ordinalPosition": 3,
          "description": null,
          "creationDate": null,
          "modificationDate": null,
          "additionalProperties": []
        },
        {
          "uuid": null,
          "name": "tripStatusEvent",
          "type": null,
          "ordinalPosition": 0,
          "description": null,
          "creationDate": null,
          "modificationDate": null,
          "additionalProperties": []
        },
        {
          "uuid": null,
          "name": "tripStatusEvent.source_system",
          "type": "string",
          "ordinalPosition": 4,
          "description": null,
          "creationDate": null,
          "modificationDate": null,
          "additionalProperties": []
        },
        {
          "uuid": null,
          "name": "tripStatusEvent.event_type",
          "type": "enum",
          "ordinalPosition": 2,
          "description": null,
          "creationDate": null,
          "modificationDate": null,
          "additionalProperties": []
        }
      ],
      "system": null,
      "isConsentView": null,
      "isHidden": null,
      "tableType": "TOPIC",
      "additionalProperties": []
    }
  ]
}
```

### Entities Async Api V2

```json
{
  "physicalEntities": [
    {
      "uuid": null,
      "schema": null,
      "name": "transportmng.tripexecution.devents.status",
      "description": "This topic contains all the *domain events* related to `Trip` entity",
      "dataSet": null,
      "creationDate": null,
      "modificationDate": null,
      "physicalFields": [
        {
          "uuid": null,
          "name": "tripStatusEvent.id",
          "type": "string",
          "ordinalPosition": 1,
          "description": null,
          "creationDate": null,
          "modificationDate": null,
          "additionalProperties": []
        },
        {
          "uuid": null,
          "name": "tripStatusEvent",
          "type": "avro/binary",
          "ordinalPosition": 0,
          "description": null,
          "creationDate": null,
          "modificationDate": null,
          "additionalProperties": []
        },
        {
          "uuid": null,
          "name": "tripStatusEvent.event_timestamp",
          "type": "string",
          "ordinalPosition": 3,
          "description": null,
          "creationDate": null,
          "modificationDate": null,
          "additionalProperties": []
        },
        {
          "uuid": null,
          "name": "tripStatusEvent.source_system",
          "type": "string",
          "ordinalPosition": 4,
          "description": null,
          "creationDate": null,
          "modificationDate": null,
          "additionalProperties": []
        },
        {
          "uuid": null,
          "name": "tripStatusEvent.event_type",
          "type": "enum",
          "ordinalPosition": 2,
          "description": null,
          "creationDate": null,
          "modificationDate": null,
          "additionalProperties": []
        }
      ],
      "system": null,
      "isConsentView": null,
      "isHidden": null,
      "tableType": "TOPIC",
      "additionalProperties": []
    },
    {
      "uuid": null,
      "schema": null,
      "name": "transportmng.tripexecution.devents.position",
      "description": "This topic contains all the *domain events* related position tracking of `Trip` entity",
      "dataSet": null,
      "creationDate": null,
      "modificationDate": null,
      "physicalFields": [
        {
          "uuid": null,
          "name": "tripPositionEvent.id",
          "type": "string",
          "ordinalPosition": 1,
          "description": null,
          "creationDate": null,
          "modificationDate": null,
          "additionalProperties": []
        },
        {
          "uuid": null,
          "name": "tripPositionEvent.position",
          "type": "string",
          "ordinalPosition": 2,
          "description": null,
          "creationDate": null,
          "modificationDate": null,
          "additionalProperties": []
        },
        {
          "uuid": null,
          "name": "tripPositionEvent.event_timestamp",
          "type": "string",
          "ordinalPosition": 3,
          "description": null,
          "creationDate": null,
          "modificationDate": null,
          "additionalProperties": []
        },
        {
          "uuid": null,
          "name": "tripPositionEvent",
          "type": "avro/binary",
          "ordinalPosition": 0,
          "description": null,
          "creationDate": null,
          "modificationDate": null,
          "additionalProperties": []
        }
      ],
      "system": null,
      "isConsentView": null,
      "isHidden": null,
      "tableType": "TOPIC",
      "additionalProperties": []
    }
  ]
}

```

