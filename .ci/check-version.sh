#!/usr/bin/env bash

set -euo pipefail

current_version=$(cat .version)
test "$current_version" != "$1"
