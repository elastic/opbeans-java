#Multi-Stage build

#Build application stage
#We need maven.
FROM maven:3.6.3-jdk-11
ARG JAVA_AGENT_BRANCH=master
ARG JAVA_AGENT_REPO=elastic/apm-agent-java

WORKDIR /usr/src/java-app

#build the application
ADD . /usr/src/java-code
WORKDIR /usr/src/java-code/opbeans

#Bring the latest frontend code
COPY --from=opbeans/opbeans-frontend:latest /app/build src/main/resources/public

RUN mvn -q --batch-mode package \
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

#build the agent
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

#Run application Stage#We only need java
FROM adoptopenjdk:11-jre-hotspot

RUN export
RUN apt-get -qq update \
 && apt-get install --no-install-recommends -y -qq curl \
 && rm -f /var/cache/apt/archives/*.deb /var/cache/apt/archives/partial/*.deb /var/cache/apt/*.bin || true
WORKDIR /app
COPY --from=0 /usr/src/java-app/*.jar ./
RUN curl --location --output opentelemetry-javaagent-all.jar "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.4.0/opentelemetry-javaagent-all.jar"

LABEL \
    org.label-schema.schema-version="1.0" \
    org.label-schema.vendor="Elastic" \
    org.label-schema.name="opbeans-java-otel" \
    org.label-schema.version="1.23.0" \
    org.label-schema.url="https://hub.docker.com/r/opbeans/opbeans-java" \
    org.label-schema.vcs-url="https://github.com/elastic/opbeans-java" \
    org.label-schema.license="MIT"

CMD java -javaagent:/app/opentelemetry-javaagent-all.jar -Dspring.profiles.active=${OPBEANS_JAVA_PROFILE:-}\
                                        -Dserver.port=${OPBEANS_SERVER_PORT:-}\
                                        -Dserver.address=${OPBEANS_SERVER_ADDRESS:-0.0.0.0}\
                                        -Dspring.datasource.url=${DATABASE_URL:-}\
                                        -Dspring.datasource.driverClassName=${DATABASE_DRIVER:-}\
                                        -Dspring.jpa.database=${DATABASE_DIALECT:-}\
                                        -Dotel.resource.attributes=service.name=opbeans-java-otel \
                                        -Dotel.exporter.otlp.endpoint=${ELASTIC_APM_SERVER_URL:-http://localhost:8200}\
                                        -jar /app/app.jar
