#!/usr/bin/env bash
set -euxo pipefail

AGENT_VERSION="${1:?"Please set the java agent version"}"

# Update agent dependencies
cd opbeans
./mvnw -B versions:use-dep-version -DdepVersion="${AGENT_VERSION}" -Dincludes=co.elastic.apm:apm-agent-api
