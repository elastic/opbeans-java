#!/usr/bin/env bash
set -euxo pipefail

AGENT_VERSION="${1:?"Please set the java agent version"}"

# Update agent dependencies
cd opbeans
./mvnw -B versions:use-dep-version -DdepVersion="${AGENT_VERSION}" -Dincludes=co.elastic.apm:apm-agent-api

## Bump agent version in the Dockerfile
sed -ibck "s#\(org.label-schema.version=\)\(\".*\"\)\(.*\)#\1\"${AGENT_VERSION}\"\3#g" ../Dockerfile

# Commit changes
git add pom.xml ../Dockerfile
git commit -m "Bump version ${AGENT_VERSION}"
