#!/bin/bash

set -euo pipefail

JAVA_AGENT=''

APP_OPTS=""
APP_OPTS="${APP_OPTS} -Dspring.profiles.active=${OPBEANS_JAVA_PROFILE:-}"
APP_OPTS="${APP_OPTS} -Dserver.port=${OPBEANS_SERVER_PORT:-}"
APP_OPTS="${APP_OPTS} -Dserver.address=${OPBEANS_SERVER_ADDRESS:-0.0.0.0}"
APP_OPTS="${APP_OPTS} -Dspring.datasource.url=${DATABASE_URL:-}"
APP_OPTS="${APP_OPTS} -Dspring.datasource.driverClassName=${DATABASE_DRIVER:-}"
APP_OPTS="${APP_OPTS} -Dspring.jpa.database=${DATABASE_DIALECT:-}"

case "${APM_AGENT_TYPE}" in
    "opentelemetry")
        echo "opentelemetry"
        JAVA_AGENT="-javaagent:/app/opentelemetry-javaagent.jar"
        APP_OPTS="${APP_OPTS} -Dotel.instrumentation.runtime-metrics.enabled=true"
        ;;
    "elasticapm")
        echo "elasticapm"
        JAVA_AGENT="-javaagent:/app/elastic-apm-agent.jar"
        ;;
    *)
        echo "unknown agent type $APM_AGENT_TYPE"
        exit 1
        ;;
esac

java \
    ${JAVA_AGENT} \
    ${APP_OPTS} \
    -jar ./app.jar
