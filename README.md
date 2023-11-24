# reactive-people-mongodb

#### MongoDB version for my Reactive Spring Talk 12/2020.

[![Java CI with Maven](https://github.com/ksbrwsk/reactive-people-mongodb/actions/workflows/maven.yml/badge.svg)](https://github.com/ksbrwsk/reactive-people-mongodb/actions/workflows/maven.yml)

**Prerequisites:**

* [Java 21](https://openjdk.net/)
* [Apache Maven](https:http://maven.apache.org/)
* [Docker](https://www.docker.com/)

Application properties can be configured in

```bash
reactive-people-mongodb/src/main/resources/application.yml
```

#### How to build and run

Type

```bash
mvn package
mvn spring-boot:run
```

**Themes:**

* Spring Webflux
* Router Functions/Handler Functions
* reactive Database Connectivity (MongoDB)
* Unit Testing
* Integration Testing with Testcontainers (MongoDB)
