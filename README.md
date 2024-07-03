# Open Data Mesh Observer Blindata

Observer adapter for [blindata.io](https://blindata.io/).

Blindata is a SaaS platform that leverages Data Governance and Compliance to empower your Data Management projects. The
purpose of this adapter is to keep the data catalog within Blindata constantly updated. Upon the occurrence of a
creation, deletion, or modification of a dataproduct, Blindata is immediately and automatically notified to ensure that
its catalog remains aligned.

*_This project has dependencies from the
project [odm-platform](https://github.com/opendatamesh-initiative/odm-platform)_

      address: The address of ODM Notification Service

## Contents

1. [General Schema Annotations](#general-schema-annotations)
    - [Entity](#entity)
    - [Fields](#fields)
2. [Metadata Mapping in Blindata](#metadata-mapping-in-blindata)
    - [Systems](#systems)
    - [DatastoreAPI](#datastoreapi)
        - [JSON Schema](#json-schema)
            - [Physical Entity](#physical-entity)
            - [Physical Field](#physical-field)
    - [AsyncAPI](#asyncapi)
        - [Avro](#avro)
            - [Physical Entity](#from-avro-to-physical-entity)
            - [Physical Field](#from-avro-to-physical-field)
3. [Examples](#examples)
    - [DatastoreAPI](#datastore-api-example)
        - [Single Entity](#single-entity)
        - [Multiple Entities](#multiple-entities)
     - [AsyncAPI](#async-api)
        - [Raw Port Async Api V3](#raw-port-async-api-v3)
        - [Raw Port Async Api V2](#raw-port-async-api-v2)
        - [Entities Async Api V3](#entities-async-api-v3)
        - [Entities Async Api V2](#entities-async-api-v2)
4. [Prerequisites](#prerequisites)
5. [Dependencies](#dependencies)
    - [Clone Dependencies Repository](#clone-dependencies-repository)
    - [Compile Dependencies](#compile-dependencies)
6. [Run Locally](#run-locally)
    - [Clone Repository](#clone-repository)
    - [Compile Project](#compile-project)
    - [Run Application](#run-application)
7. [Run with Docker](#run-with-docker)
    - [Clone Repository](#clone-repository-1)
    - [Compile Project](#compile-project-1)
    - [Build Image](#build-image)
    - [Run Application](#run-application-1)
    - [Stop Application](#stop-application)
8. [Run with Docker Compose](#run-with-docker-compose)
    - [Clone Repository](#clone-repository-2)
    - [Compile Project](#compile-project-2)
    - [Build Image](#build-image-1)
    - [Run Application](#run-application-2)
    - [Stop Application](#stop-application-1)
9. [Test It](#test-it)
    - [REST Services](#rest-services)
    - [Blindata Configuration](#blindata-configuration)
    - [ODM Configuration](#odm-configuration)

## General Schema Annotations

Schema annotations describe the properties and metadata associated with entity and field
schemas. These annotations help to better define and document data schemas, improving the understanding and usability of
data structures.

### Entity

Entities annotations help to describe the general characteristics and metadata of a data structure. In the table below there's a list of annotations each one have a detailed description, its requirement status, and support for JSONSchema and Avro.

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

Fields annotations help to describe the general characteristics and metadata of a data structure. In the table below there's a list of annotations each one have a detailed description, its requirement status, and support for JSONSchema and Avro.

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

## Mapping in Blindata

The observer supports the use of two specifications: Datastore API and Async API. For Datastore API the schema format
supported is JSON, while for Async API is AVRO.

### Systems

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

### DatastoreApi

##### Physical Entity

This section describes the mapping of schema annotations to physical entity properties within the system. Physical entities represent the main data structures, such as tables or views, in the data store.
Physical entities are mappen from "schema.content" in case of single entity "schema.content.entities" in case of multiple
entities.

| Schema Annotation      | Physical Entity Property | Description                                                         | Mandatory |
|------------------------|--------------------------|---------------------------------------------------------------------|-----------|
| `schema.name`          | name                     | The name of the physical entity.                                    | ✔️        |
| `schema.physicalType`  | tableType                | Specifies the type of the table (e.g., TABLE, VIEW).                | -         |
| `schema.description`   | description              | A detailed description of the physical entity.                      | -         |
| `schema.status`        | add.prop                 | The current status of the physical entity (e.g., active, inactive). | -         |
| `schema.tags`          | add.prop                 | Tags for categorizing the physical entity.                          | -         |
| `schema.domain`        | add.prop                 | The domain to which the physical entity belongs.                    | -         |
| `schema.contactPoints` | add.prop                 | Contact information for the physical entity.                        | -         |
| `schema.scope`         | add.prop                 | The scope of the physical entity within the system.                 | -         |
| `schema.externalDocs`  | add.prop                 | Links to external documentation related to the physical entity.     | -         |

#### Physical Field

This section details the mapping of schema annotations to physical field properties. Physical fields represent the individual attributes or columns within a physical entity, providing specific data points within the entity.
Physical fields are mapped from "properties" field inside each entity definition.

| Schema Annotation          | Physical Field Property | Description                          | Mandatory |
|----------------------------|-------------------------|--------------------------------------|-----------|
| `properties.name`          | name                    | Object name                          | ✔️        |
| `properties.physicalType` | type                    | Physical type of the object          | -         |
| `properties.comments` | description             | Additional comments about the object | -         |
| `properties.kind`   | add.prop                | Object type (e.g., TABULAR)          | -         |
| `properties.status` | add.prop                | Object status (e.g., TESTING)        | -         |
| `properties.tags`   | add.prop                | Tags associated with the object      | -         |
| `properties.owner`  | add.prop                | Owner of the object                  | -         |
| `properties.domain` | add.prop                | Domain to which the object belongs   | -         |
| `properties.contactpoints` | add.prop                | Contact points related to the object | -         |
| `properties.scope`  | add.prop                | Scope of the object (e.g., private)  | -         |
| `properties.version` | add.prop                | Version of the object                | -         |
| `properties.displayName` | add.prop                | Display name of the object           | -         |
| `properties.description` | add.prop                | Description of the object            | -         |

### AsyncApi

##### From Avro to Physical Entity

Physical entities are mapped from "channel" section of the descriptor.

| Schema Annotation      | Physical Entity Property | Mandatory | Default Value |
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

##### From Avro to Physical Field
Physical entities can be find in "channel.message" section of the descriptor.

| Schema Annotation              | Physical Field Property | Mandatory | Default Value |
|--------------------------------|-------------------------|-----------|---------------|
| `channel.message.id`           | name                    | ✔️        | -             |
| `channel.message.contentType`  | type                    | -         | TOPIC         |
| `channel.message.description`  | description             | -         | -             |
| `channel.message.name`         | add.prop                | -         | -             |
| `channel.message.title`        | add.prop                | -         | -             |
| `channel.message.tags`         | add.prop                | -         | -             |
| `channel.message.externalDocs` | add.prop                | -         | -             |
| `channel.message.summary`      | add.prop                | -         | -             |
| `channel.message.address`      | add.prop                | -         | -             |

## Examples

### Datastore Api Example

#### Single Entity
This section provides examples of how to represent a single entity using the Data Store API. It demonstrates the application of schema annotations to define multiple physical entities and their fields, illustrating how the mappings are structured and implemented in practice.
rom the descriptor the observer will create a physical entity called "airline_freq" with physical fields :"id", "apt_dst", "flt_freq", "apt_org", "airline_code"

```json
{
  "...other properties": {
  },
  "outputPorts": [
    {
      "fullyQualifiedName": "urn:org.opendatamesh:dataproducts:airlinedemo:outputports:flight_frequency_db",
      "entityType": "outputPorts",
      "name": "flight_frequency_db",
      "version": "1.0.0",
      "displayName": "flight_frequency_db",
      "description": "Target database for airlines data. MySQL database.",
      "promises": {
        "platform": "MySQL_demoBlindataMySql",
        "serviceType": "datastore-services",
        "api": {
          "name": "flightFrequencyApi",
          "version": "1.0.0",
          "specification": "datastoreapi",
          "specificationVersion": "1.0.0",
          "definition": {
            "datastoreapi": "1.0.0",
            "info": {
              "databaseName": "airlinedemo",
              "nameSpace": "nome_schema",
              "title": "flight_frequency Data",
              "summary": "This API exposes the current flight_frequency data of each `Airline` entity",
              "version": "1.0.0",
              "datastoreName": "flight_frequency"
            },
            "services": {
              "development": {
                "serverInfo": {
                  "$ref": ""
                },
                "serverVariables": {
                  "host": ""
                }
              }
            },
            "schema": {
              "name": "airline_freq",
              "kind": "TABULAR",
              "comments": "commento",
              "examples": [
                {
                  "id": 1,
                  "name": "name"
                }
              ],
              "status": "TESTING",
              "tags": "tag",
              "owner": "owner",
              "domain": "domain",
              "contactpoints": "contact",
              "scope": "private",
              "version": "1.0.0",
              "fullyQualifiedName": "urn:dsas:com.company-xyz:tables:airline.airline_freq",
              "displayName": "Trips Status Table",
              "description": "The table that stores the updated status of each trip",
              "physicalType": "VIEW",
              "properties": {
                "id": {
                  "type": "string",
                  "description": "The flight identifier.",
                  "name": "ID",
                  "kind": "ATTRIBUTE",
                  "required": true,
                  "displayName": "Identifier",
                  "summary": "Inline description",
                  "comments": "comment",
                  "examples": [
                    "1234567",
                    "988654"
                  ],
                  "status": "statusa",
                  "tags": [
                    "tag1",
                    "tag2"
                  ],
                  "externalDocs": "https://",
                  "default": null,
                  "isClassified": true,
                  "classificationLevel": "",
                  "isUnique": true,
                  "isNullable": false,
                  "pattern": "a regex",
                  "format": "named pattern e.g. email",
                  "enum": [
                    "VALORE1",
                    "VALORE2"
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
                  "clusterKeyPosition": 2
                },
                "airline_code": {
                  "name": "airline_code",
                  "fullyQualifiedName": "urn:dsas:com.company-xyz:tables:airline.airline_freq.id",
                  "displayName": "Airline ID",
                  "type": "VARCHAR",
                  "dataLength": "50",
                  "columnConstraint": "PRIMARY_KEY",
                  "ordinalPosition": 1
                },
                "apt_org": {
                  "name": "apt_org",
                  "fullyQualifiedName": "urn:dsas:com.company-xyz:tables:airline.airline_freq.apt_org",
                  "displayName": "Origin",
                  "dataType": "VARCHAR",
                  "dataLength": "50",
                  "columnConstraint": "PRIMARY_KEY",
                  "ordinalPosition": 2
                },
                "apt_dst": {
                  "name": "apt_dst",
                  "fullyQualifiedName": "urn:dsas:com.company-xyz:tables:airline.airline_freq.apt_dst",
                  "displayName": "Destination",
                  "dataType": "VARCHAR",
                  "dataLength": "50",
                  "columnConstraint": "PRIMARY_KEY",
                  "ordinalPosition": 3
                },
                "flt_freq": {
                  "name": "flt_freq",
                  "fullyQualifiedName": "urn:dsas:com.company-xyz:tables:airline.airline_freq.trip_status.flt_freq",
                  "displayName": "Flight Frequency",
                  "dataType": "INTEGER",
                  "columnConstraint": "NOT_NULL",
                  "ordinalPosition": 4
                }
              }
            }
          }
        }
      }
    }
    },
    // ... outputPort's othe properties
  ]
  // ... data product's other properties
}
```
#### Multiple Entities
This section provides examples of how to represent multiple entities using the Data Store API. It demonstrates the application of schema annotations to define multiple physical entities and their fields, illustrating how the mappings are structured and implemented in practice.
From the descriptor the observer will create two physical entities called "Customer" and "Payments" each one with just a physical field "id".

```json
{
  "datastoreapi": "1.0.0",
  "info": {
    "databaseName": "airlinedemo",
    "nameSpace": "nome_schema",
    "title": "flight_frequency Data",
    "summary": "This API exposes the current flight_frequency data of each `Airline` entity",
    "version": "1.0.0",
    "datastoreName": "flight_frequency"
  },
  "services": {
    "development": {
      "serverInfo": {
        "$ref": "#components.serverInfo.flightFrequencyStoreServerInfo"
      },
      "serverVariables": {
        "host": "35.52.55.12"
      }
    }
  },
  "schema": {
    "id": 1,
    "name": "ab334d27-41e7-3622-88f7-6dd5a200ae30",
    "version": "1.0.0",
    "mediaType": "application/json",
    "content": {
      "entities": [
        {
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
        },
        {
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
      ]
    }
  },
  "components": {
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
    }
  }
}
```

### Async Api

#### Raw Port Async Api V3
This section illustrates an example of an Async API version 3 configuration for a Trip Status Streaming API. The API is used to stream events related to the Trip entity. It describes the schema for the messages, including the fields and their types.
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

#### Raw Port Async Api V2
This section provides an example of an Async API version 2 configuration for a Trip Status Streaming API. It includes details on the servers, channels, and message schemas used for streaming events related to the Trip entity.
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

#### Entities Async Api V3

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

#### Entities Async Api V2

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

## REST services

You can invoke REST endpoints through *OpenAPI UI* available at the following url:

* [http://localhost:9002/api/v1/up/observer/swagger-ui/index.html](http://localhost:9002/api/v1/up/observer/swagger-ui/index.html)

## Blindata configuration

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
```

## ODM configuration

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
```

