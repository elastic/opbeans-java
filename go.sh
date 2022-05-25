#!/bin/bash

java -version

cd opbeans

#./mvnw clean package

export ELASTIC_APM_SERVICE_NAME=opbeans-debug
export ELASTIC_APM_SERVER_URL=http://localhost:8200
export ELASTIC_APM_APPLICATION_PACKAGES=co.elastic.apm.opbeans
export ELASTIC_APM_ENABLE_LOG_CORRELATION=true
export ELASTIC_APM_ENABLE_EXPERIMENTAL_INSTRUMENTATIONS=true
export ELASTIC_APM_ENVIRONMENT=debug

dl_agent_version=1.31.0
curl -O https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/${dl_agent_version}/elastic-apm-agent-${dl_agent_version}.jar
agent_jar=elastic-apm-agent-${dl_agent_version}.jar

app_jar=target/opbeans-0.0.1-SNAPSHOT.jar

java \
-javaagent:${agent_jar} \
-jar ${app_jar}

cd -
