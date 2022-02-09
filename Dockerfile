#Multi-Stage build
ARG APM_AGENT_TYPE=elasticapm

#Build application stage
#We need maven.
FROM maven:3.8.4-jdk-11

ARG JAVA_AGENT_BRANCH=master
ARG JAVA_AGENT_REPO=elastic/apm-agent-java
ARG OTEL_JAVA_AGENT_VERSION=v1.10.1

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

#build the elastic APM agent
WORKDIR /usr/src/java-agent-code
RUN curl -L https://github.com/$JAVA_AGENT_REPO/archive/$JAVA_AGENT_BRANCH.tar.gz | tar --strip-components=1 -xz
RUN mvn -q --batch-mode clean package \
  -Dmaven.repo.local=.m2 \
  --no-transfer-progress \
  -Dmaven.wagon.http.retryHandler.count=3 \
  -Dhttps.protocols=TLSv1.2 \
  -Dhttp.keepAlive=false \
  -Dmaven.javadoc.skip=true \
  -DskipTests=true \
  -Dmaven.gitcommitid.skip=true

RUN export JAVA_AGENT_BUILT_VERSION=$(mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec) \
    && cp -v /usr/src/java-agent-code/elastic-apm-agent/target/elastic-apm-agent-${JAVA_AGENT_BUILT_VERSION}.jar /usr/src/java-app/elastic-apm-agent.jar

#Download the opentelemetry agent
RUN curl -L https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/$OTEL_JAVA_AGENT_VERSION/opentelemetry-javaagent.jar --output /usr/src/java-app/opentelemetry-javaagent.jar

#Run application Stage
#We only need java
FROM adoptopenjdk:14-jre-hotspot AS base

RUN export
RUN apt-get -qq update \
 && apt-get install --no-install-recommends -y -qq curl \
 && rm -f /var/cache/apt/archives/*.deb /var/cache/apt/archives/partial/*.deb /var/cache/apt/*.bin || true

WORKDIR /app
COPY --from=0 /usr/src/java-app/*.jar ./

LABEL \
    org.label-schema.schema-version="1.0" \
    org.label-schema.vendor="Elastic" \
    org.label-schema.name="opbeans-java" \
    org.label-schema.version="1.28.4" \
    org.label-schema.url="https://hub.docker.com/r/opbeans/opbeans-java" \
    org.label-schema.vcs-url="https://github.com/elastic/opbeans-java" \
    org.label-schema.license="MIT"

FROM base AS branch-version-elasticapm
CMD java -javaagent:/app/elastic-apm-agent.jar -Dspring.profiles.active=${OPBEANS_JAVA_PROFILE:-}\
                                        -Dserver.port=${OPBEANS_SERVER_PORT:-}\
                                        -Dserver.address=${OPBEANS_SERVER_ADDRESS:-0.0.0.0}\
                                        -Dspring.datasource.url=${DATABASE_URL:-}\
                                        -Dspring.datasource.driverClassName=${DATABASE_DRIVER:-}\
                                        -Dspring.jpa.database=${DATABASE_DIALECT:-}\
                                        -jar /app/app.jar

FROM base AS branch-version-opentelemetry
CMD java -javaagent:opentelemetry-javaagent.jar\
                                      -Dspring.profiles.active=${OPBEANS_JAVA_PROFILE:-}\
                                      -Dserver.port=${OPBEANS_SERVER_PORT:-}\
                                      -Dserver.address=${OPBEANS_SERVER_ADDRESS:-0.0.0.0}\
                                      -Dspring.datasource.url=${DATABASE_URL:-}\
                                      -Dspring.datasource.driverClassName=${DATABASE_DRIVER:-}\
                                      -Dspring.jpa.database=${DATABASE_DIALECT:-}\
                                      -Dotel.instrumentation.runtime-metrics.enabled=true\
                                      -jar /app/app.jar

FROM branch-version-${APM_AGENT_TYPE} AS final
RUN echo "Start build with ${APM_AGENT_TYPE}"
