#
# Makefile for golang docker registry
#


HIDE ?= @
SHELL := /bin/bash

DOCKER_IMAGE ?= onelogin/registry
DOCKER_CONTAINER ?= registry
AWS_KEY ?= 
AWS_SECRET ?= 
AWS_REGION ?= us-east-1


all:

registry-s3:
	$(HIDE)docker run -d \
	    -e REGISTRY_STORAGE=s3 \
            -e STORAGE_PATH=/registry \
            -e REGISTRY_STORAGE_S3_BUCKET=central-docker-registry \
            -e REGISTRY_STORAGE_S3_ACCESSKEY=$(AWS_KEY) \
            -e REGISTRY_STORAGE_S3_SECRETKEY=$(AWS_SECRET) \
            -e REGISTRY_STORAGE_S3_REGION=$(AWS_REGION) \
            -e DEBUG=True \
            -e SEARCH_BACKEND=sqlalchemy \
            -p 5000:5000 \
            --restart=always \
            --name $(DOCKER_CONTAINER) \
            -d $(DOCKER_CONTAINER):2

registry:
	$(HIDE)docker run -d \
	    -e REGISTRY_STORAGE_FILESYSTEM_ROOTDIRECTORY=/var/lib/registry \
            -e DEBUG=True \
            -e SEARCH_BACKEND=sqlalchemy \
            -v /registry/docker:/var/lib/registry \
            -p 5000:5000 \
            --restart=always \
            --name $(DOCKER_CONTAINER) \
            -d $(DOCKER_CONTAINER):2

bases := docker
base-images:
	$(HIDE)$(foreach dir,$(bases),(pushd ../base/$(dir) && make image && popd) &&):

clean-images:
	-$(HIDE)docker rm `docker ps -a -q`
	-$(HIDE)docker rmi `docker images -q`

dirs := docker
images:
	$(HIDE)$(foreach dir,$(dirs),(pushd ../$(dir) && make image && make push && popd) &&):

stop:
	$(HIDE)docker stop $(DOCKER_CONTAINER)
	-$(HIDE)docker rm $(DOCKER_CONTAINER)

enter:
	$(HIDE)docker exec -it $(DOCKER_CONTAINER) /bin/bash
