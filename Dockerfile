# Stage 1: build Registry Client
FROM maven:3-openjdk-11-slim as build

WORKDIR /workspace/app

# Clone Platform project and build it (the client module is a dependency for this project)
RUN apt-get update
RUN apt-get -y install git
RUN git clone https://github.com/opendatamesh-initiative/odm-platform-pp-services.git

WORKDIR /workspace/app/odm-platform-pp-services

RUN mvn clean install -DskipTests

WORKDIR /workspace/app

RUN git clone https://github.com/opendatamesh-initiative/odm-platform-up-services-meta-blindata.git

WORKDIR /workspace/app/odm-platform-up-services-meta-blindata

RUN mvn clean install -DskipTests


# Stage 2: App executable
FROM openjdk:11-jre-slim

RUN apt-get update && apt-get install -y wget gpg lsb-release zip curl

ARG SPRING_PROFILES_ACTIVE=docker
ARG SPRING_PORT=8595
ARG JAVA_OPTS
ARG DATABASE_URL
ARG DATABASE_USERNAME
ARG DATABASE_PASSWORD
ARG FLYWAY_SCHEMA=flyway_metaservice
ARG FLYWAY_SCRIPTS_DIR=postgres
ARG H2_CONSOLE_ENABLED=false
ARG H2_CONSOLE_PATH=h2-console
ARG BLINDATA_URL
ARG BLINDATA_USER
ARG BLINDATA_PWD
ARG BLINDATA_TENANT
ARG BLINDATA_ROLE
ENV SPRING_PROFILES_ACTIVE ${SPRING_PROFILES_ACTIVE}
ENV SPRING_PORT ${SPRING_PORT}
ENV JAVA_OPTS ${JAVA_OPTS}
ENV DATABASE_URL ${DATABASE_URL}
ENV DATABASE_USERNAME ${DATABASE_USERNAME}
ENV DATABASE_PASSWORD ${DATABASE_PASSWORD}
ENV FLYWAY_SCHEMA ${FLYWAY_SCHEMA}
ENV FLYWAY_SCRIPTS_DIR ${FLYWAY_SCRIPTS_DIR}
ENV H2_CONSOLE_ENABLED ${H2_CONSOLE_ENABLED}
ENV H2_CONSOLE_PATH ${H2_CONSOLE_PATH}
ENV BLINDATA_URL ${BLINDATA_URL}
ENV BLINDATA_USER ${BLINDATA_USER}
ENV BLINDATA_PWD ${BLINDATA_PWD}
ENV BLINDATA_TENANT ${BLINDATA_TENANT}
ENV BLINDATA_ROLE ${BLINDATA_ROLE}

COPY --from=build  /workspace/app/odm-platform-up-services-meta-blindata/meta-service-blindata/target/odm-platform-up-meta-service-blindata-*.jar /app/

RUN ln -s -f /usr/share/zoneinfo/Europe/Rome /etc/localtime

CMD java $JAVA_OPTS -jar /app/odm-platform-up-meta-service-blindata*.jar --spring.profiles.active=$SPRING_PROFILES_ACTIVE

EXPOSE $SPRING_PORT