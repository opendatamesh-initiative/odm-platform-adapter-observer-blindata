# Open Data Mesh Meta Service Adapter for Blindata

Meta service adapter for [blindata.io](https://blindata.io/)

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




