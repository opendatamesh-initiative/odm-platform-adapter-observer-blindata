# Open Data Mesh Meta Service Adapter for Blindata

Meta service adapter for [blindata.io](https://blindata.io/)
Blindata is a SAAS platform that leverages Data Governance and Compliance to empower your Data Management projects.
The purpose of this adapter is to keep the business glossary within Blindata constantly updated. Upon the occurrence of a creation, deletion, or modification of a dataproduct, Blindata is immediately and automatically notified to ensure that its catalog remains aligned.

# Run it

## Prerequisites
The project requires the following dependencies:

* Java 11
* Maven 3.8.6

## Run locally

### Clone repository
Clone the repository and move to the project root folder

```bash
git clone git@github.com:opendatamesh-initiative/odm-platform-up-services-meta-blindata.git
cd odm-platform-up-services-meta-blindata
```
### Compile project
Compile the project:

```bash
mvn clean package
```

### Run application
Run the application:

```bash
java -jar meta-service-server/target/odm-platform-up-meta-service-server-1.0.0.jar
```

## Run with Docker

*WIP*

## Run with Docker Compose

*WIP*

# Test it

## REST services

You can invoke REST endpoints through *OpenAPI UI* available at the following url:

* [http://localhost:8595/api/v1/up/metaservice/swagger-ui/index.html](http://localhost:8595/api/v1/up/metaservice/swagger-ui/index.html)

## Database 

If the application is running using an in memory instance of H2 database you can check the database content through H2 Web Console available at the following url:

* [http://localhost:8595/api/v1/up/metaservice/h2-console](http://localhost:8595/api/v1/up/metaservice/h2-console)

In all cases you can also use your favourite sql client providing the proper connection parameters


## Blindata configuration

In order to connect with Blindata, you must specified some important values in file application.yml
```yaml
blindata:
    url: the url where Blindata application is reachable 
    user: the username used to log in Blindata
    password: the password to connect in Blindata
    tenantUUID: the tenant where you have to operate
    roleUuid: A possible role identifier. You need this identifier to create or update responsibilities in Blindata
```
# Spring Profiles and configuration

The application can be immediately run with the default profile.
The default uses one profile: dev. 
Custom configuration can be created by overriding these profiles.

# Postgres Configuration
If you want a postgres configuration insert the following code to your profile: 
```yaml
datasource:
    url: url of your postgres db
    username: username of the postgres db
    password: password of the postgres db
```

# Flyway Configuration:
If you want to manage your migration with flyway insert the following code to your active profile: 
```yaml
flyway:
    enabled: true
    url: url of the db
    user: sername of the postgres db
    password: password of the postgres db
    schemas: schema where
    locations: classpath:db/migrations/directory with your migration

```
