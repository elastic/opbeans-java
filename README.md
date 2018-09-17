# opbeans-java
This is an implementation of the [Opbeans Demo app](http://opbeans.com) in Java as an [Spring Boot](https://projects.spring.io/spring-boot/) application . It uses the same
database schema as the [Node](https://github.com/opbeat/opbeans) version.

By default it will use a pre-populated in memory H2 database.

To run the application run the following command from the `opbeans` folder:

    ./mvnw  spring-boot:run

## Test with APM 
To test with APM, including Server, Kibana and Elasticsearch, use the provided docker compose file by running the command```
    
    docker-composer up


## Customize Database

Database can be overridden by using system properties and overriding values from the application property files:

    ./mvnw  spring-boot:run -Dspring.jpa.database=POSTGRESQL -Dspring.datasource.driverClassName=org.postgresql.Driver -Dspring.datasource.url=jdbc:postgresql://localhost/opbeans?user=postgres&password=verysecure 

Another possible way is to create a diferent property file like application-customdb.properties and enabling it with a profile:

    ./mvnw  spring-boot:run -Dspring.profiles.active=customdb

## Demo notes

The application has a built-in bug that you can trigger by
navigating to the path `/is-it-coffee-time`.