#Multi-Stage build

#Build application stage
#We need maven.
FROM maven:3.8.4-jdk-11
ARG JAVA_AGENT_BRANCH=master
ARG JAVA_AGENT_REPO=elastic/apm-agent-java

WORKDIR /usr/src/java-app

#build the application
ADD . /usr/src/java-code
WORKDIR /usr/src/java-code/opbeans

#Bring the latest frontend code
COPY --from=opbeans/opbeans-frontend:latest /app src/main/resources/public

RUN mvn -X --batch-mode package \
  -DskipTests \
  -Dmaven.repo.local=.m2 \
  --no-transfer-progress \
  -Dmaven.wagon.http.retryHandler.count=3 \
  -Dhttps.protocols=TLSv1.2 \
  -Dhttp.keepAlive=false \
  -Dmaven.javadoc.skip=true \
  -DskipTests=true \
  -Dmaven.gitcommitid.skip=true
RUN cp -v /usr/src/java-code/opbeans/target/*.jar /usr/src/java-app/app.jar

#Run application Stage
#We only need java

FROM adoptopenjdk:14-jre-hotspot

RUN export
RUN apt-get -qq update \
 && apt-get install --no-install-recommends -y -qq curl \
 && rm -f /var/cache/apt/archives/*.deb /var/cache/apt/archives/partial/*.deb /var/cache/apt/*.bin || true

ARG OTEL_JAVA_AGENT_VERSION=v1.10.1

WORKDIR /app
COPY --from=0 /usr/src/java-app/*.jar ./
RUN curl -L https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/$OTEL_JAVA_AGENT_VERSION/opentelemetry-javaagent.jar --output opentelemetry-javaagent.jar


LABEL \
    org.label-schema.schema-version="1.0" \
    org.label-schema.vendor="Elastic" \
    org.label-schema.name="opbeans-java" \
    org.label-schema.version="1.28.1" \
    org.label-schema.url="https://hub.docker.com/r/opbeans/opbeans-java" \
    org.label-schema.vcs-url="https://github.com/elastic/opbeans-java" \
    org.label-schema.license="MIT"

CMD java -javaagent:opentelemetry-javaagent.jar\
                                      -Dspring.profiles.active=${OPBEANS_JAVA_PROFILE:-}\
                                      -Dserver.port=${OPBEANS_SERVER_PORT:-}\
                                      -Dserver.address=${OPBEANS_SERVER_ADDRESS:-0.0.0.0}\
                                      -Dspring.datasource.url=${DATABASE_URL:-}\
                                      -Dspring.datasource.driverClassName=${DATABASE_DRIVER:-}\
                                      -Dspring.jpa.database=${DATABASE_DIALECT:-}\
                                      -Dotel.instrumentation.runtime-metrics.enabled=true\
                                      -jar /app/app.jar
