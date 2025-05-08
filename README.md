# Open Data Mesh Observer Blindata

Observer adapter for [blindata.io](https://blindata.io/).

Blindata is a SaaS platform that leverages Data Governance and Compliance to empower your Data Management projects. The
purpose of this adapter is to keep the data catalog within Blindata constantly updated. Upon the occurrence of a
creation, deletion, or modification of a dataproduct, Blindata is immediately and automatically notified to ensure that
its catalog remains aligned.

## Contents

<!-- TOC -->

* [Open Data Mesh Observer Blindata](#open-data-mesh-observer-blindata)
    * [Contents](#contents)
* [Event Handling](#event-handling)
    * [Configuration](#configuration)
* [Data Product Validation](#data-product-validation)
* [Mapping data product descriptor into Blindata](#mapping-data-product-descriptor-into-blindata)
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
* [Examples](#examples)
    * [Datastore Api Example](#datastore-api-example)
    * [Async Api](#async-api)
        * [Raw Port Async Api V3](#raw-port-async-api-v3)
        * [Raw Port Async Api V2](#raw-port-async-api-v2)
        * [Entities Async Api V3](#entities-async-api-v3)
        * [Entities Async Api V2](#entities-async-api-v2)
    * [Input port dependency](#input-port-dependency)
* [Blindata credentials configurations](#blindata-credentials-configurations)
    * [Using Api Key](#using-api-key)
    * [Using Oauth2](#using-oauth2)
* [Run the Project](#run-the-project)
    * [Prerequisites](#prerequisites)
    * [Dependencies](#dependencies)
        * [Clone dependencies repository](#clone-dependencies-repository)
        * [Compile dependencies](#compile-dependencies)
    * [Run locally](#run-locally)
        * [Clone repository](#clone-repository)
        * [Compile project](#compile-project)
        * [Run application](#run-application)
    * [Run with Docker](#run-with-docker)
        * [Clone repository](#clone-repository-1)
        * [Compile project](#compile-project-1)
        * [Build image](#build-image)
        * [Run application](#run-application-1)
        * [Stop application](#stop-application)
    * [Run with Docker Compose](#run-with-docker-compose)
        * [Clone repository](#clone-repository-2)
        * [Compile project](#compile-project-2)
        * [Build image](#build-image-1)
        * [Run application](#run-application-2)
        * [Stop application](#stop-application-1)
* [Test it](#test-it)
    * [REST services](#rest-services)
    * [Blindata configuration](#blindata-configuration)
    * [ODM configuration](#odm-configuration)

<!-- TOC -->

# Event Handling

The observer adapter for Blindata is designed to react to Notification Events that are emitted by the Open Data Mesh
Product Platform.

The observer can subscribe to notification events whit two level of granularity:

1. The event type. This can be:
    - DATA_PRODUCT_CREATED
    - DATA_PRODUCT_UPDATED
    - DATA_PRODUCT_DELETED
    - DATA_PRODUCT_VERSION_CREATED
    - DATA_PRODUCT_VERSION_DELETED
    - DATA_PRODUCT_ACTIVITY_CREATED
    - DATA_PRODUCT_ACTIVITY_STARTED
    - DATA_PRODUCT_ACTIVITY_COMPLETED
    - DATA_PRODUCT_TASK_CREATED
    - DATA_PRODUCT_TASK_STARTED
    - DATA_PRODUCT_TASK_COMPLETED
    - POLICY_CREATED
    - POLICY_UPDATED
    - POLICY_DELETED

2. The event content using a SpeL expression.
   E.g. An expression to capture an event regarding a data product ( match done by its fullyQualifiedName ).
   ```
   #root['afterState']['info']['fullyQualifiedName'] == 'urn:org.opendatamesh:dataproducts:hrs:users'
   ```
   ```json
    {
      "id": "",
      "type": "",
      "entityId": "",
      "beforeState": {},
      "afterState": {
        //data product descriptor here
      }, 
      "time": ""
   }
   ```

P.A. its is always mandatory to specify the event type.

For every subscription it is possible to specify a set of actions that the observer will perform.
These are:

1. **DATA_PRODUCT_UPLOAD**. It uploads the main information of the data product to Blindata and assigns ownership
   responsibilities to the designated user.
    - Supported event types:
        - DATA_PRODUCT_CREATED
        - DATA_PRODUCT_VERSION_CREATED
2. **DATA_PRODUCT_VERSION_UPLOAD**. It uploads the data product's ports metadata along with the assets specified in the
   descriptor api definitions.
    - Supported event types:
        - DATA_PRODUCT_VERSION_CREATED
        - DATA_PRODUCT_ACTIVITY_COMPLETED
3. **QUALITY_UPLOAD**: It uploads the quality checks specified in the data product's ports metadata.
    - Supported event types:
        - DATA_PRODUCT_VERSION_CREATED
        - DATA_PRODUCT_ACTIVITY_COMPLETED
4. **STAGES_UPLOAD**. It uploads the data product's stages which are defined inside the lifecycleInfo of the descriptor.
    - Supported event types:
        - DATA_PRODUCT_VERSION_CREATED
        - DATA_PRODUCT_ACTIVITY_COMPLETED
5. **DATA_PRODUCT_REMOVAL**. It removes the data product from Blindata.
    - Supported event types:
        - DATA_PRODUCT_DELETED
6. **POLICIES_UPLOAD**. It gathers all policy evaluation results for the specified data product and uploads them to
   Blindata.
    - Supported event types:
        - DATA_PRODUCT_VERSION_CREATED
        - DATA_PRODUCT_ACTIVITY_COMPLETED
7. **POLICIES_ALIGN**. Aligns Blindata Governance Policy Suites, Policies and Policies Implementations using Odm Policy
   External Context field.
    - Supported Event Types
        - POLICY_CREATED: creates the Suite, Policy and Implementation if not present.
        - POLICY_UPDATED: updates the Suite, Policy and Implementation.
        - POLICY_DELETED: deletes only the Policy and its Implementation if present.

### Configuration

The configuration can be written in the application.yml or passed as an argument at startup.
An example of a configuration can be:

```yaml
blindata:
  eventHandlers: "[
      {
        \"eventType\": \"DATA_PRODUCT_VERSION_CREATED\",
        \"filter\":  \"\",
        \"activeUseCases\": [
          \"DATA_PRODUCT_UPLOAD\",
          \"DATA_PRODUCT_VERSION_UPLOAD\",
          \"STAGES_UPLOAD\",
          \"POLICIES_UPLOAD\"
        ]
      },
      {
        \"eventType\": \"DATA_PRODUCT_VERSION_DELETED\",
        \"filter\": \"\",
        \"activeUseCases\": [
          \"DATA_PRODUCT_REMOVAL\"
        ]
      },
       {
        \"eventType\": \"DATA_PRODUCT_ACTIVITY_COMPLETED\",
        \"filter\": \"\",
        \"activeUseCases\": [
          \"STAGES_UPLOAD\",
          \"POLICIES_UPLOAD\"
        ]
      }
    ]"
```

P.A. Actions are automatically ordered based on the correct sequence of execution.

# Data Product Validation

The Observer includes a feature that automatically generates a Policy within the Policy Service, which is evaluated
before creating a DataProduct or DataProductVersion.

In the configuration, the policy can be set to blocking, ensuring that the data product is created only if it meets
compliance requirements. It verifies that the data product contains all necessary elements required by Blindata and can
be uploaded without issues.

Below is an example configuration:

```yaml
blindata:
  validator:
    active: true
    policyEngine:
      name: <policy engine name>
    policy:
      name: <policy name>
      blocking: true (or false)
```

# Mapping data product descriptor into Blindata

Given a descriptor, the following elements are created in Blindata:

- Systems
- Physical Entities
- Physical Fields

This mapping ensures that all data structures and their components are accurately represented and can be monitored or
managed within Blindata, providing a seamless integration between the descriptor and the Blindata environment.

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

- **Quality Suite Code**: `<data product domain>::<data product name>`
- **Quality Suite Name**: `<data product domain>::<data product display name>`  
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

## Input port dependency

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

# Blindata credentials configurations

First, you need to set the URL for communicating with Blindata:

```yaml
blindata:
  url: https://app.blindata.io (an example)
```

Next, configure the Observer with the appropriate authentication method to interact with Blindata's APIs.
For more details, refer to the [official documentation](https://help.blindata.io/administration/authentication-methods/)

## Using Api Key

If you want to use Api Key authentication method, you must add the following application properties.

```yaml
blindata:
  [ ... ]
  user: <The user identifier>
  password: <The user password>
  tenantUUID: <The Blindata tenant Uuid>
  [ ... ]
```

## Using Oauth2

If you want to use the Oauth2 protocol to retrieve a token from an external provider (e.g. Microsoft Entra ID, Google
Cloud Identity) and then
authenticate on Blindata API, you can configure one of the following methods.

**Using shared secret**

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

**Using a certificate**
The supported certificate format is `.pem` (Base64 format).

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

# Run the Project

## Prerequisites

The project requires the following dependencies:

* Java 11
* Maven 3.8.6
* Project [odm-platform](https://github.com/opendatamesh-initiative/odm-platform)

## Dependencies

This project needs some artifacts from the odm-platform project.

### Clone dependencies repository

Clone the repository and move to the project root folder:

  ```bash
  git clone https://github.com/opendatamesh-initiative/odm-platform.git
  cd odm-platform

```

### Compile dependencies

Compile the project:

```bash
mvn clean install -DskipTests
```

## Run locally

*_Dependencies must have been compiled to run this project._

### Clone repository

Clone the repository and move to the project root folder

```bash
git clone git@github.com:opendatamesh-initiative/odm-platform-adapter-observer-blindata.git
cd odm-platform-adapter-observer-blindata
```

### Compile project

Compile the project:

```bash
mvn clean package spring-boot:repackage -DskipTests
```

### Run application

Run the application:

```bash
java -jar observer-blindata-server/target/odm-platform-adapter-observer-blindata-server-1.0.0.jar
```

## Run with Docker

*_Dependencies must have been compiled to run this project._

### Clone repository

Clone the repository and move it to the project root folder

```bash
git clone git@github.com:opendatamesh-initiative/odm-platform-adapter-observer-blindata.git
cd odm-platform-adapter-observer-blindata
```

Here you can find the Dockerfile which creates an image containing the application by directly copying it from the build
executed locally (i.e. from `target` folder).

### Compile project

You need to first execute the build locally by running the following command:

```bash
mvn clean package spring-boot:repackage -DskipTests
```

### Build image

Build the Docker image of the application and run it.

*Before executing the following commands:

* assign the value of arguments `BLINDATA_URL`, `BLINDATA_USER`, `BLINDATA_PWD`, `BLINDATA_TENANT` and `BLINDATA_ROLE`.

```bash
docker build -t odm-observer-blindata-app . -f Dockerfile \
   --build-arg BLINDATA_URL=<blindata-url> \
   --build-arg BLINDATA_USER=<blindata-user> \
   --build-arg BLINDATA_PWD=<blindata-pwd> \
   --build-arg BLINDATA_TENANT=<blindata-tenant> \
   --build-arg BLINDATA_ROLE=<blindata-role>
```

### Run application

Run the Docker image.

```bash
docker run --name odm-observer-blindata-app -p 9002:9002 odm-observer-blindata-app
```

### Stop application

```bash
docker stop odm-observer-blindata-app
```

To restart a stopped application execute the following commands:

```bash
docker start odm-observer-blindata-app
```

To remove a stopped application to rebuild it from scratch execute the following commands :

```bash
docker rm odm-observer-blindata-app
```

## Run with Docker Compose

*_Dependencies must have been compiled to run this project._

### Clone repository

Clone the repository and move it to the project root folder

```bash
git clone git@github.com:opendatamesh-initiative/odm-platform-adapter-observer-blindata.git
cd odm-platform-adapter-observer-blindata
```

### Compile project

Compile the project:

```bash
mvn clean package spring-boot:repackage -DskipTests
```

### Build image

Build the docker-compose images of the application and a default PostgreSQL DB (v11.0).

Before building it, create a `.env` file in the root directory of the project similar to the following one:

```.dotenv
SPRING_PORT=9002
BLINDATA_URL=<blindata-url>
BLINDATA_USER=<blindata-user>
BLINDATA_PWD=<blindata-pwd>
BLINDATA_TENANT=<blindata-tenant-uuid>
BLINDATA_ROLE=<blindata-role-uuid>
REGISTRY_ACTIVE=true
REGISTRY_HOSTNAME=localhost
REGISTRY_PORT=8001
POLICY_ACTIVE=true
POLICY_HOSTNAME=localhost
POLICY_PORT=8005
NOTIFICATION_ACTIVE=true
NOTIFICATION_HOSTNAME=localhost
NOTIFICATION_PORT=8006
```

*_Blindata parameters will be explained below_

Then, build the docker-compose file:

```bash
docker-compose build
```

### Run application

Run the docker-compose images.

```bash
docker-compose up
```

### Stop application

Stop the docker-compose images

```bash
docker-compose down
```

To restart a stopped application execute the following commands:

```bash
docker-compose up
```

To rebuild it from scratch execute the following commands :

```bash
docker-compose build --no-cache
```

# Test it

### REST services

You can invoke REST endpoints through *OpenAPI UI* available at the following url:

* [http://localhost:9002/api/v1/up/observer/swagger-ui/index.html](http://localhost:9002/api/v1/up/observer/swagger-ui/index.html)

### Blindata configuration

In order to connect with Blindata, you must specify some important values in file `application.yml` (or in `.env` file
if you're running the application with docker-compose, or as build arguments if you're running the application through
Docker)

```yaml
blindata:
  url: the url where Blindata application is reachable
  user: the username used to log in Blindata
  password: the password to connect in Blindata
  tenantUUID: the tenant where you have to operate
  roleUuid: A possible role identifier. You need this identifier to create or update responsibilities in Blindata (value optional)
  systemNameRegex: optional regex to extract system name from schema (value optional)
  systemTechnologyRegex: optional regex to extract system technology from schema (value optional)
  enableAsync: (true/false, default false) If enabled, the observer will use the asynchronous endpoints of Blindata API. This allows
    to process big data product descriptors without failing due to connection timeouts.
  dataProducts:
    assetsCleanup: (true/false, default true) Enables or disables the cleanup of deprecated assets associated with data product ports. Additionally, when set to true, any quality checks defined in the Data Product Quality Suite but no longer listed in the quality section of the data product descriptor will also be automatically disabled.
  issueManagement:
    policies:
      active: (true/false, default true) If set to false, all the issue policies that are uploaded on Blindata are set to disabled.
```

### ODM configuration

In order to connect with ODM microservices, you must specify some important values in file `application.yml` (or
in `.env` file if you're running the application with docker-compose, or as build arguments if you're running the
application through Docker)

```yaml
odm:
  productPlane:
    policyService:
      active: Whether the ODM Policy Service is active or not
      address: The address of ODM Policy Service
    registryService:
      active: Whether the ODM Registry Service is active or not
      address: The address of ODM Registry Service
    notificationService:
      active: Whether the ODM Notification Service is active or not
      address: The address of ODM Notification Service
      subscribeWithName: (default "BLINDATA") The name used by the observer to subscribe itself on the Notification Service.       
```

