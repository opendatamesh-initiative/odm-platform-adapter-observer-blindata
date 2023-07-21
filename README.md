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
   --build-arg BLINDATA_TENTANT=<blindata-tenant> \
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
   --build-arg BLINDATA_TENTANT=<blindata-tenant> \
   --build-arg BLINDATA_ROLE=<blindata-role>
```

### Run application
Run the Docker image.

*Note: Before executing the following commands remove the argument `--net host` if the database is not running on `localhost`*

**MySql**
```bash
docker run --name odm-meta-service-mysql-app -p 8595:8595 --net host odmp-mysql-app
```

**Postgres**
```bash
docker run --name odm-meta-service-postgres-app -p 8585:8585 --net host odmp-postgres-app
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
SPRING_PORT=8595
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

* [http://localhost:8595/api/v1/up/metaservice/swagger-ui/index.html](http://localhost:8595/api/v1/up/metaservice/swagger-ui/index.html)

## Database 

If the application is running using an in memory instance of H2 database you can check the database content through H2 Web Console available at the following url:

* [http://localhost:8595/api/v1/up/metaservice/h2-console](http://localhost:8595/api/v1/up/metaservice/h2-console)

In all cases you can also use your favourite sql client providing the proper connection parameters


## Blindata configuration

In order to connect with Blindata, you must specified some important values in file `application.yml` (or in `.env` file if you're running the application with docker-compose, or as build arguments if you're running the application through Docker)
```yaml
blindata:
    url: the url where Blindata application is reachable 
    user: the username used to log in Blindata
    password: the password to connect in Blindata
    tenantUUID: the tenant where you have to operate
    roleUuid: A possible role identifier. You need this identifier to create or update responsibilities in Blindata
```
