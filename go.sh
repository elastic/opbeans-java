#!/bin/env bash

java -version

cd opbeans

./mvnw clean package

export ELASTIC_APM_SERVICE_NAME=opbeans-debug
export ELASTIC_APM_SERVER_URL=http://localhost:8200
export ELASTIC_APM_APPLICATION_PACKAGES=co.elastic.apm.opbeans
export ELASTIC_APM_ENABLE_LOG_CORRELATION=true
export ELASTIC_APM_ENABLE_EXPERIMENTAL_INSTRUMENTATIONS=true
export ELASTIC_APM_ENVIRONMENT=debug

# get released agent
dl_agent_version=1.31.0
curl -s -o elastic-apm-agent.jar "https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/${dl_agent_version}/elastic-apm-agent-${dl_agent_version}.jar"

# get latest snapshot
curl -s -o elastic-apm-agent.jar --location 'https://apm-ci.elastic.co/job/apm-agent-java/job/apm-agent-java-mbp/job/main/lastSuccessfulBuild/artifact/src/github.com/elastic/apm-agent-java/elastic-apm-agent/target/elastic-apm-agent-1.31.1-SNAPSHOT.jar'

agent_jar=elastic-apm-agent.jar
app_jar=target/opbeans-0.0.1-SNAPSHOT.jar
agent_opts=-javaagent:${agent_jar}

#debug_opts='-agentlib:jdwp=transport=dt_socket,server=n,address=localhost:5005,suspend=y'

java \
${debug_opts:-} \
${agent_opts:-} \
-jar ${app_jar}

cd -
