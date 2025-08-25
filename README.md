# Spring Batch + Spring REST API + Dashboard (Spring Boot version - 2.5.6)

Spring Boot + Spring Batch project with:
- **Tasklet-based Job**
- **Chunk-oriented Job**
- **REST API**
- Web dashboard (Thymeleaf) to monitor, run, and restart jobs

## Features
Launch jobs with parameters (via REST endpoints or UI)
View running / completed executions
- Step-level details
- Stop / restart running jobs
Pluggable ItemReaders:
- FlatFileItemReader (CSV / delimited)
- JdbcCursorItemReader (MySQL)
- ItemReaderAdapter (consumes external REST API)

## Requirements
- Java 17+
- Maven 3+
- MySQL or H2 Database

## How to run
```bash
mvn clean install
mvn spring-boot:run
