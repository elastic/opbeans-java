#!/usr/bin/env bash
set -uxeo pipefail

GREN_GITHUB_TOKEN=${GREN_GITHUB_TOKEN:?"missing GREN_GITHUB_TOKEN"}

gren release --token="${GREN_GITHUB_TOKEN}" --override -c .grenrc.js -t all --debug
