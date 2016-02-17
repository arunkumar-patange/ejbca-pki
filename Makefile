#
# Makefile
# postgres
#
#  1. make image
#  2. make install
#  3. make start
#
#

SHELL := /bin/bash
HIDE ?= @
DOCKER_IMAGE ?= onelogin/ejbca
DOCKER_CONTAINER ?= ejbca

-include ./registry/registry.mk

PHONY: postgresdb

image:
	$(HIDE) docker build -t $(DOCKER_IMAGE) -f Dockerfile.prod
	$(MAKE) _deploy

_deploy:
	$(HIDE) echo 'should be called only once'
	$(HIDE)docker run --name ant-deploy \
		--link postgres \
		$(DOCKER_IMAGE)
	$(HIDE)docker commit ant-deploy $(DOCKER_IMAGE)
	$(HIDE)docker rm ant-deploy

start:
	$(HIDE)docker run -it --name $(DOCKER_CONTAINER) \
		--link postgres \
		-p 8080:8080 \
		-p 8443:8443 \
		-p 8442:8442 \
		-p 9990:9990 \
		-d $(DOCKER_IMAGE) \
		/ejbca/src/run.sh

enter:
	#$(HIDE)docker attach $(DOCKER_CONTAINER)
	$(HIDE)docker exec -it $(DOCKER_CONTAINER) /bin/bash

stop:
	-$(HIDE)docker rm ant-deploy
	$(HIDE)docker stop $(DOCKER_CONTAINER)
	$(HIDE)docker rm $(DOCKER_CONTAINER)

postgresdb:
	$(HIDE)docker run --name postgres -e POSTGRES_PASSWORD=postgres -d postgres

sh:
	$(HIDE)eval $(docker env docker-gui)

clean-all:
	-$(HIDE)docker ps -aq | xargs docker stop
	-$(HIDE)docker ps -aq | xargs docker rm
	-$(HIDE)docker images -q| xargs docker rmi
