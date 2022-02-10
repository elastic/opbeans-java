#!/bin/sh

case $APM_AGENT_TYPE in
  "opentelemetry")
  echo "opentelemetry"
  java -javaagent:/app/opentelemetry-javaagent.jar\
                                        -Dspring.profiles.active=${OPBEANS_JAVA_PROFILE:-}\
                                        -Dserver.port=${OPBEANS_SERVER_PORT:-}\
                                        -Dserver.address=${OPBEANS_SERVER_ADDRESS:-0.0.0.0}\
                                        -Dspring.datasource.url=${DATABASE_URL:-}\
                                        -Dspring.datasource.driverClassName=${DATABASE_DRIVER:-}\
                                        -Dspring.jpa.database=${DATABASE_DIALECT:-}\
                                        -Dotel.instrumentation.runtime-metrics.enabled=true\
                                        -jar ./app.jar
  ;;
  "elasticapm")
  echo "elasticapm"
  java -javaagent:/app/elastic-apm-agent.jar\
                                        -Dspring.profiles.active=${OPBEANS_JAVA_PROFILE:-}\
                                        -Dserver.port=${OPBEANS_SERVER_PORT:-}\
                                        -Dserver.address=${OPBEANS_SERVER_ADDRESS:-0.0.0.0}\
                                        -Dspring.datasource.url=${DATABASE_URL:-}\
                                        -Dspring.datasource.driverClassName=${DATABASE_DRIVER:-}\
                                        -Dspring.jpa.database=${DATABASE_DIALECT:-}\
                                        -jar ./app.jar
  ;;
  *)
  echo "unknown agent type $APM_AGENT_TYPE"
  ;;
  esac
fi
