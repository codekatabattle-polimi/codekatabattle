# CodeKataBattle - Backend

This is the backend of the MVP of the CodeKataBattle application. It is a RESTful application made with Spring Boot.
Please refer to the [ITD](https://github.com/codekatabattle-polimi/OrciuoloVitelloGiallongo/tree/master/DeliveryFolder) for more in-depth specifications regarding the implementation.

## Getting started

### Prerequisites

- [PostgreSQL](https://www.postgresql.org/download/) installed on the system.
- [JDK 17 or 21](https://www.oracle.com/java/technologies/downloads/#java17) installed on the system.

## Installing and running locally

Run the following commands:

```shell
./mvnw install
./mvnw spring-boot:run
```

Alternatively, it is possible to use IntelliJ IDEA to run the application and manage dependencies directly.

## Building for production

Run the following command:

```shell
./mvnw compile
```

The build output will be located in the `target/` directory.

## Running integration tests

Before running integration tests, ensure that you have [Docker](https://www.docker.com/) installed and running on your system.
Docker is needed because the integration tests use a PostgreSQL database running in a Docker container, automatically provisioned by [Testcontainers](https://testcontainers.com/).

Run the following command:

```shell
export CKB_GITHUB_TEST_PAT="{{Replace with your own PAT here}}"
./mvnw test
```

A GitHub PAT is needed in order to run the integration tests. You can learn how to create one [here](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens).

Without a valid PAT, the integration tests will fail with the following exception:
```
java.lang.IllegalArgumentException: Could not resolve placeholder 'CKB_GITHUB_TEST_PAT' in value "${CKB_GITHUB_TEST_PAT}"
```
