PORT ?= 8000
IMAGE ?= opbeans/opbeans-java
VERSION ?= latest
LTS_ALPINE ?= 12-alpine
AGENT_VERSION=$(shell cat .version)

.PHONY: help
.DEFAULT_GOAL := help

help: ## Display this help text
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

all: build test

build: ## Build docker image
	@docker build --file Dockerfile --tag=${IMAGE}:${VERSION} .

bats: ## Install bats in the project itself
	@git clone https://github.com/sstephenson/bats.git

prepare-test: bats ## Prepare the bats dependencies
	@docker pull node:${LTS_ALPINE}
	@mkdir -p target
	@git submodule sync
	@git submodule update --init --recursive

test: prepare-test ## Run the tests
	@echo "Tests are in progress, please be patient"
	@PORT=${PORT} bats/bin/bats --tap tests | tee target/results.tap
	@docker run --rm -v "${PWD}":/usr/src/app -w /usr/src/app node:${LTS_ALPINE} \
					sh -c "npm install tap-xunit -g && cat target/results.tap | tap-xunit --package='co.elastic.opbeans' > target/junit-results.xml"

publish: build ## Publish docker image
	@docker push "${IMAGE}:${VERSION}"

clean: ## Clean autogenerated files/folders
	@rm -rf bats
	@rm -rf target

create-release: ## Create git tag given the APM Agent version
	@if [ -z "$(shell git tag -l v$(AGENT_VERSION))" ]; then \
		echo "creating tag v$(AGENT_VERSION)"; \
		gh release create "v$(AGENT_VERSION)" --title="$(AGENT_VERSION)" --generate-notes; \
	else \
		echo "git tag v$(AGENT_VERSION) already exists"; \
	fi
