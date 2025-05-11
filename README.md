# Transactional Event Bridge

## Overview
Transactional Event Bridge is a lightweight demo that shows the different ways to publish events based on transaction status.

Supported technologies:
* JDBC
* R2DBC

The demo is based on the Spring Boot 3.4.5 release and uses the following technologies:
* JDBC + Spring Data JPA
* R2DBC + Spring Data R2DBC
* Spring Events
* Flyway
* Spring Web / WebFlux
* Testcontainers
* Docker Compose

The messaging is handled via Spring Events, which allows for a simple and effective way to publish events 
within the same application context.

Please run `docker-compose up` to start the PostgreSQL database.

## JDBC

The event listener can simply tap into Spring's transaction management and listen for events that 
are published after a successful transaction commit.

You can run the JDBC demo by executing the following command:
```bash
./gradlew :jdbc:bootRun
```

## R2DBC

The R2DBC demo uses a different approach. It leverages the `TransactionalOperator` 
to manage transactions in a reactive way.

You can run the R2DBC demo by executing the following command:
```bash
./gradlew :r2dbc:bootRun
```
