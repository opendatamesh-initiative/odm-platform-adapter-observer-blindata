# DEPRECATO #

Repo spostato su github

* [odm-platform-up-services-meta-blindata](https://github.com/opendatamesh-initiative/odm-platform-up-services-meta-blindata)

## Summary ##

* Content: Open Data Mesh - Utility Plane - Meta Service - Blindata Meta Service
* Version: 0.1

## Descrizione
This repository contains:

* a springboot project for the backend implementation of Meta Service that is integrated with Blindata
* a Docker project for its execution

## Requirements ##
* Java 11
* Maven
* IDE (recommended IntelliJ)
* Git

## Deploy
**Requirement: Docker**

1. Change the current directory in your terminal to the main directory of the repository (where the Dockerfile file is placed)
2. To create the docker image of the application execute the command `sudo docker build -t blindata-metaservice .`
3. To launch the dockerized version of the application execute the command `sudo docker run --name blindata-metaservice -p8084:8084 -d blindata-metaservice`

## Usage ##
Once the application is started, you may find the API documentation and a Swagger at this [URL](http://localhost:8084/swagger-ui/index.html).

## Contributors ##

* Pietro La Torre

# Notes #

## Currently implemented APIs

1. **POST** to create a load
2. **GET** to get a list of loads (that may be filtered by _id dataproduct_)
3. **GET** to get a load given its id
4. **DELETE** to delete a load given its id
5. **PUT** to change some details of a data product already registered in blindata

## Useful Links
1. [Swagger UI](http://localhost:8084/swagger-ui/index.html)
2. [h2 console](http://localhost:8084/h2-console/)