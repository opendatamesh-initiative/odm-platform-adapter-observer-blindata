FROM maven:3-openjdk-11-slim

ADD ./ /code/app
WORKDIR /code/app

RUN apt-get update && apt-get install -y wget gpg lsb-release zip

ENV APP_NAME=blindata-metaservice

RUN mvn clean install -DskipTests \
    && mkdir /app \
    && cp /code/app/target/$APP_NAME-*.jar /app

WORKDIR /$APP_NAME

RUN ln -s -f /usr/share/zoneinfo/Europe/Rome /etc/localtime

#ENV JDBC_URL=jdbc:mysql://localhost:3306/teambuildingdb?createDatabaseIfNotExist=true
#ENV DB_USERNAME=root
#ENV DB_PASSWORD=my-secret-pw

ENV JAVA_OPTS=""
CMD java $JAVA_OPTS -jar /app/$APP_NAME-*.jar --spring.profiles.active=docker