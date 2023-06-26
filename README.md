[![Build Status](https://github.com/elastic/opbeans-java/actions/workflows/test.yml/badge.svg)](https://github.com/elastic/opbeans-java/actions/workflows/test.yml)

# opbeans-java
This is an implementation of the [Opbeans Demo app](http://opbeans.com) in Java as an [Spring Boot](https://projects.spring.io/spring-boot/) application . It uses the same
database schema as the [Node](https://github.com/opbeat/opbeans) version.

By default it will use a pre-populated in memory H2 database.

To run the application run the following command from the `opbeans` folder:

    ./mvnw  spring-boot:run

## Run locally
To run locally, including Server, Kibana and Elasticsearch, use the provided docker compose file by running the command
```bash
docker-compose up
```

## Run with Elastic Cloud

0. Start Elastic Cloud [trial](https://www.elastic.co/cloud/elasticsearch-service/signup) (if you don't have it yet)
1. Add environmental variables `ELASTIC_CLOUD_ID` and `ELASTIC_CLOUD_CREDENTIALS` (in format `login:password`)
2. Add environmental variable `STACK_VERSION` to match your deployed Elasticsearch version.
3. Run
```bash
docker-compose -f docker-compose-elastic-cloud.yml up
```

## Testing locally

The simplest way to test this demo is by running:

```bash
make test
```

Tests are written using [bats](https://github.com/sstephenson/bats) under the tests dir

## Publishing to dockerhub locally

Publish the docker image with

```bash
VERSION=1.2.3 make publish
```

NOTE: VERSION refers to the tag for the docker image which will be published in the registry

## Customize Database

Database can be overridden by using system properties and overriding values from the application property files:

    ./mvnw  spring-boot:run -Dspring.jpa.database=POSTGRESQL -Dspring.datasource.driverClassName=org.postgresql.Driver -Dspring.datasource.url=jdbc:postgresql://localhost/opbeans?user=postgres&password=verysecure

Another possible way is to create a different property file like application-customdb.properties and enabling it with a profile:

    ./mvnw  spring-boot:run -Dspring.profiles.active=customdb

## Demo notes

The application has a built-in bug that you can trigger by
navigating to the path `/is-it-coffee-time`.

## Agent mode (elasticapm/opentelemetry)

The Opbeans Java can use the APM Agent Java or the OpenTelemetry java implementation
in order to choose one of other you have to set the environment variable APM_AGENT_TYPE
to `elasticapm` when you use the APM Agent Java and `opentelemetry` to use OpenTelemetry java implementation.
Finally, there is also the `none` value that will make Opbeans run without any instrumentation agent.

When OpenTelemetry agent is enabled, the OpenTelemetry environment variables should be properly set to allow the application to send spans created through manual instrumentation.
see the [docker-compose](./docker-compose.yml) file for more details about the environment variables, and [OpenTelemetry SDK Autoconfigure](https://github.com/open-telemetry/opentelemetry-java/blob/main/sdk-extensions/autoconfigure/README.md) for more details.

