# Open Data Mesh Meta Service Adapter for Blindata

Meta service adapter for [blindata.io](https://blindata.io/)
Blindata is a SAAS platform that leverages Data Governance and Compliance to empower your Data Management projects.
The purpose of this adapter is to keep the business glossary within Blindata constantly updated. Upon the occurrence of a creation, deletion, or modification of a dataproduct, Blindata is immediately and automatically notified to ensure that its catalog remains aligned.

*_This project have dependencies from the project [odm-platform](https://github.com/opendatamesh-initiative/odm-platform)_

# Run it

## Prerequisites
The project requires the following dependencies:

* Java 11
* Maven 3.8.6
* Project  [odm-platform](https://github.com/opendatamesh-initiative/odm-platform)

## Dependencies
This project need some artifacts from the odm-platform project.

### Clone dependencies repository
Clone the repository and move to the project root folder

```bash
git git clone https://github.com/opendatamesh-initiative/odm-platform.git
cd odm-platform-pp-services
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
git clone git@github.com:opendatamesh-initiative/odm-platform-up-services-meta-blindata.git
cd odm-platform-up-services-meta-blindata
```

### Compile project
Compile the project:

```bash
mvn clean package spring-boot:repackage -DskipTests
```

### Run application
Run the application:

```bash
java -jar meta-service-blindata/target/odm-platform-up-meta-service-blindata-1.0.0.jar
```

## Run with Docker
*_Dependencies must have been compiled to run this project._

### Clone repository
Clone the repository and move it to the project root folder

```bash
git clone git@github.com:opendatamesh-initiative/odm-platform-up-services-meta-blindata.git
cd odm-platform-up-services-meta-blindata
```

Here you can find the Dockerfile which creates an image containing the application by directly copying it from the build executed locally (i.e. from `target` folder).

### Compile project
You need to first execute the build locally by running the following command:

```bash
mvn clean package spring-boot:repackage -DskipTests
```

### Run database
The image generated from Dockerfile contains only the application. It requires a database to run properly. The supported databases are MySql and Postgres. If you do not already have a database available, you can create one by running the following commands:

**MySql**
```bash
docker run --name odm-meta-service-mysql-db -d -p 3306:3306  \
   -e MYSQL_DATABASE=ODMNOTIFICATION \
   -e MYSQL_ROOT_PASSWORD=root \
   mysql:8
```

**Postgres**
```bash
docker run --name odm-meta-service-postgres-db -d -p 5432:5432  \
   -e POSTGRES_DB=odm-meta-service-db \
   -e POSTGRES_PASSWORD=postgres \
   postgres:11-alpine
```

Check that the database has started correctly:

**MySql**
```bash
docker logs odm-meta-service-mysql-db
```

*Postgres*
```bash
docker logs odm-meta-service-postgres-db
```
### Build image
Build the Docker image of the application and run it.

*Before executing the following commands:
* change properly the value of arguments `DATABASE_USERNAME`, `DATABASE_PASSWORD` and `DATABASE_URL`. Reported commands already contains right argument values if you have created the database using the commands above.
* assign the value of arguments `BLINDATA_URL`, `BLINDATA_USER`, `BLINDATA_PWD`, `BLINDATA_TENANT` and `BLINDATA_ROLE`.

**MySql**
```bash
docker build -t odm-meta-service-mysql-app . -f Dockerfile \
   --build-arg DATABASE_URL=jdbc:mysql://localhost:3306/ODMNOTIFICATION \
   --build-arg DATABASE_USERNAME=root \
   --build-arg DATABASE_PASSWORD=root \
   --build-arg FLYWAY_SCRIPTS_DIR=mysql \
   --build-arg BLINDATA_URL=<blindata-url> \
   --build-arg BLINDATA_USER=<blindata-user> \
   --build-arg BLINDATA_PWD=<blindata-pwd> \
   --build-arg BLINDATA_TENANT=<blindata-tenant> \
   --build-arg BLINDATA_ROLE=<blindata-role>
```

**Postgres**
```bash
docker build -t odm-meta-service-postgres-app . -f Dockerfile \
   --build-arg DATABASE_URL=jdbc:postgresql://localhost:5432/odm-meta-service-db \
   --build-arg DATABASE_USERNAME=postgres \
   --build-arg DATABASE_PASSWORD=postgres \
   --build-arg BLINDATA_URL=<blindata-url> \
   --build-arg BLINDATA_USER=<blindata-user> \
   --build-arg BLINDATA_PWD=<blindata-pwd> \
   --build-arg BLINDATA_TENANT=<blindata-tenant> \
   --build-arg BLINDATA_ROLE=<blindata-role>
```

### Run application
Run the Docker image.

*Note: Before executing the following commands remove the argument `--net host` if the database is not running on `localhost`*

**MySql**
```bash
docker run --name odm-meta-service-mysql-app -p 9002:9002 --net host odm-meta-service-mysql-app
```

**Postgres**
```bash
docker run --name odm-meta-service-postgres-app -p 9002:9002 --net host odm-meta-service-postgres-app
```

### Stop application

*Before executing the following commands:
* change the DB name to `odm-meta-service-postgres-db` if you are using postgres and not mysql
* change the instance name to `odm-meta-service-postgres-app` if you are using postgres and not mysql

```bash
docker stop odm-meta-service-mysql-app
docker stop odm-meta-service-mysql-db
```
To restart a stopped application execute the following commands:

```bash
docker start odm-meta-service-mysql-db
docker start odm-meta-service-mysql-app
```

To remove a stopped application to rebuild it from scratch execute the following commands :

```bash
docker rm odm-meta-service-mysql-app
docker rm odm-meta-service-mysql-db
```

## Run with Docker Compose
*_Dependencies must have been compiled to run this project._

### Clone repository
Clone the repository and move it to the project root folder

```bash
git clone git@github.com:opendatamesh-initiative/odm-platform-up-services-meta-blindata.git
cd odm-platform-up-services-meta-blindata
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
DATABASE_NAME=odm-metaservice-db
DATABASE_PASSWORD=pwd
DATABASE_USERNAME=usr
DATABASE_PORT=5434
SPRING_PORT=9002
BLINDATA_URL=<blindata-url>
BLINDATA_USER=<blindata-user>
BLINDATA_PWD=<blindata-pwd>
BLINDATA_TENANT=<blindata-tenant-uuid>
BLINDATA_ROLE=<blindata-role-uuid>
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

* [http://localhost:9002/api/v1/up/metaservice/swagger-ui/index.html](http://localhost:9002/api/v1/up/metaservice/swagger-ui/index.html)

## Database

If the application is running using an in memory instance of H2 database you can check the database content through H2 Web Console available at the following url:

* [http://localhost:9002/api/v1/up/metaservice/h2-console](http://localhost:9002/api/v1/up/metaservice/h2-console)

In all cases you can also use your favourite sql client providing the proper connection parameters


## Blindata configuration

In order to connect with Blindata, you must specified some important values in file `application.yml` (or in `.env` file if you're running the application with docker-compose, or as build arguments if you're running the application through Docker)
```yaml
blindata:
    url: the url where Blindata application is reachable 
    user: the username used to log in Blindata
    password: the password to connect in Blindata
    tenantUUID: the tenant where you have to operate
    roleUuid: A possible role identifier. You need this identifier to create or update responsibilities in Blindata (value optional)
    odmPlatformUrl: the url where ODM platform is reachable
    systemNameRegex: optional regex to extract system name from schema (value optional)
    systemTechnologyRegex : optional regex to extract system technology from schema (value optional)
```

## Schema Definition
To define data assets connected to data product's port in Blindata, users can define a field "schema" inside data product ports.
There are two possible scenarios:
- The first one involves multiple tables within the same schema. In this case, the tables must be placed inside the 'entities' array within the 'content' field of the schema.

```json
{
   "outputPorts":[
      {     
         "entityType":"outputPorts",
         "name":"flight_frequency_db",
         "version":"1.0.0",
         "displayName":"flight_frequency_db",
         "description":"Target database for airlines data. MySQL database.",
         "promises":{
            "platform":"MySQL_demoBlindataMySql",
            "serviceType":"datastore-services",
            "api":{
               "name":"flightFrequencyApi",
               "version":"1.0.0",
               "specification":"datastoreapi",
               "specificationVersion":"1.0.0",
               "definition":{
                  "datastoreapi":"1.0.0",
                  "info":{
                     "databaseName":"airlinedemo",
                     "nameSpace":"nome_schema",
                     "title":"flight_frequency Data",
                     "summary":"This API exposes the current flight_frequency data of each `Airline` entity",
                     "version":"1.0.0",
                     "datastoreName":"flight_frequency"
                  },
                  "services":{
                     "development":{
                        "serverInfo":{
                           "$ref": ""
                        },
                        "serverVariables":{
                           "host": ""
                        }
                     }
                  },
                  "schema":{
                     "id":1,
                     "name":"ab334d27-41e7-3622-88f7-6dd5a200ae30",
                     "version":"1.0.0",
                     "mediaType":"application/json",
                     "content":{
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
                               "externalDocs": "https://",
                               "default": null,
                               "isClassified": true,
                               "classificationLevel": "LIBERO",
                               "isUnique": true,
                               "isNullable": false,
                               "pattern": "una regex",
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
                               "clusterKeyPosition": 2,
                               "ordinalPosition": 0

                             },
                             "customerName": {
                               "name": "customerName",
                               "fullyQualifiedName": "urn:dsas:com.company-xyz:tables:airline.airline_freq.id",
                               "displayName": "Customer Name",
                               "type": "VARCHAR",
                               "dataLength": "50",
                               "columnConstraint": "PRIMARY_KEY",
                               "ordinalPosition": 1
                             },
                             "customerMail": {
                               "name": "customerMail",
                               "fullyQualifiedName": "urn:dsas:com.company-xyz:tables:airline.airline_freq.apt_org",
                               "displayName": "Origin",
                               "dataType": "VARCHAR",
                               "dataLength": "50",
                               "columnConstraint": "PRIMARY_KEY",
                               "ordinalPosition": 2
                             },
                             "customerAddress": {
                               "name": "customerAddress",
                               "fullyQualifiedName": "urn:dsas:com.company-xyz:tables:airline.airline_freq.apt_dst",
                               "displayName": "Destination",
                               "dataType": "VARCHAR",
                               "dataLength": "50",
                               "columnConstraint": "FOREIGN_KEY",
                               "ordinalPosition": 3
                             }
                           }

                         },{
                           "name": "Payment",
                           "type": "object",
                           "properties": {
                             "id": {
                               "type": "string",
                               "description": "The payment identifier.",
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
                               "externalDocs": "https://",
                               "default": null,
                               "isClassified": true,
                               "classificationLevel": "LIBERO",
                               "isUnique": true,
                               "isNullable": false,
                               "pattern": "una regex",
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
                               "clusterKeyPosition": 2,
                               "ordinalPosition": 0

                             }
                           }
                         }
                       ]
                     }
                  }
               }
            }
         }
      }
      // ... outputPort's othe properties
   ]
   // ... data product's other properties
}

```
The second scenario involves a single table, and its content can be directly passed within the 'content' property.
```json
{
   "...other properties":{
   },
   "outputPorts":[
      {
         "fullyQualifiedName":"urn:org.opendatamesh:dataproducts:airlinedemo:outputports:flight_frequency_db",
         "entityType":"outputPorts",
         "name":"flight_frequency_db",
         "version":"1.0.0",
         "displayName":"flight_frequency_db",
         "description":"Target database for airlines data. MySQL database.",
         "promises":{
            "platform":"MySQL_demoBlindataMySql",
            "serviceType":"datastore-services",
            "api":{
               "name":"flightFrequencyApi",
               "version":"1.0.0",
               "specification":"datastoreapi",
               "specificationVersion":"1.0.0",
               "definition":{
                  "datastoreapi":"1.0.0",
                  "info":{
                     "databaseName":"airlinedemo",
                     "nameSpace":"nome_schema",
                     "title":"flight_frequency Data",
                     "summary":"This API exposes the current flight_frequency data of each `Airline` entity",
                     "version":"1.0.0",
                     "datastoreName":"flight_frequency"
                  },
                  "services":{
                     "development":{
                        "serverInfo":{
                           "$ref": ""
                        },
                        "serverVariables":{
                           "host": ""
                        }
                     }
                  },
                  "schema":{
                     "name": "airline_freq",
                             "kind": "TABULAR",
                             "comments": "commento",
                             "examples": [
                               {"id": 1, "name": "name"}
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
                                 "examples": ["1234567", "988654"],
                                 "status": "statusa",
                                 "tags": ["tag1", "tag2"],
                                 "externalDocs": "https://",
                                 "default": null,
                                 "isClassified": true,
                                 "classificationLevel": "",
                                 "isUnique": true,
                                 "isNullable": false,
                                 "pattern": "a regex",
                                 "format": "named pattern e.g. email",
                                 "enum": ["VALORE1", "VALORE2"],
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
            },
         }
      },
      // ... outputPort's othe properties
   ],
   // ... data product's other properties
}

```
The *'platform'* field within the *'promises'* field of the port is used to extract the name and system technology of the system to be created in Blindata.

To extract the name and technology for association, two regular expressions must be defined within the Blindata configurations, as shown in the previous section.

### Schema Table Fields:
The following fields describe an individual table within a schema.

- `name`: Object name  -  STRING
- `kind`: Object type (e.g., TABULAR) -  ENUM
- `comments`: Additional comments about the object   -  STRING
- `status`: Object status (e.g., TESTING)  -  STRING
- `tags`: Tags associated with the object - ARRAY[STRING]
- `owner`: Owner of the object -  STRING
- `domain`: Domain to which the object belongs - STRING
- `contactpoints`: Contact points related to the object - STRING
- `scope`: Scope of the object (e.g., private) -STRING
- `version`: Version of the object -STRING
- `fullyQualifiedName`: Fully qualified name of the object -STRING
- `displayName`: Display name of the object -STRING
- `description`: Description of the object -STRING
- `physicalType`: Physical type of the object -STRING
- `properties`: Map containing properties where the key is the property name, and the value is a corresponding `SchemaColumn` object.

### Schema Column Fields:

The following fields describe an individual column within a schema.

- `type`: Type of the field -STRING
- `description`: Field description -STRING
- `name`: Field name -STRING
- `kind`: Field type (e.g., ATTRIBUTE) - ENUM
- `required`: Indicates whether the field is required - BOOLEAN
- `displayName`: Display name of the field - STRING
- `summary`: Field summary - STRING
- `comments`: Additional comments -STRING
- `examples`: Field examples (list) - ARRAY[STRING]
- `status`: Field status (e.g., TESTING) - STRING
- `tags`: Tags associated with the field (list) - ARRAY[STRING]
- `externalDocs`: External documentation - STRING
- `defaultValue`: Default value of the field - STRING
- `isClassified`: Indicates whether the field is classified - BOOLEAN
- `classificationLevel`: Classification level - STRING
- `isUnique`: Indicates whether the field is unique - BOOLEAN
- `isNullable`: Indicates whether the field can be nullable - BOOLEAN
- `pattern`: Field pattern - STRING
- `format`: Field format - STRING
- `enumValues`: Enumerated values of the field (list) - ENUM
- `minLength`: Minimum length of the field - NUMBER
- `maxLength`: Maximum length of the field - NUMBER
- `contentEncoding`: Content encoding - STRING
- `contentMediaType`: Content media type - STRING
- `precision`: Field precision - NUMBER
- `scale`: Field scale - NUMBER
- `minimum`: Minimum value of the field - NUMBER
- `exclusiveMinimum`: Indicates whether the minimum value is exclusive - BOOLEAN
- `maximum`: Maximum value of the field - NUMBER
- `exclusiveMaximum`: Indicates whether the maximum value is exclusive - BOOLEAN
- `readOnly`: Indicates whether the field is read-only - BOOLEAN
- `writeOnly`: Indicates whether the field is write-only - BOOLEAN
- `physicalType`: Physical type of the field - STRING
- `partitionStatus`: Indicates the partition status - STRING
- `partitionKeyPosition`: Partition key position - NUMBER
- `clusterStatus`: Indicates the cluster status - STRING
- `clusterKeyPosition`: Cluster key position -  NUMBER
- `ordinalPosition`: Position in table -  NUMBER




