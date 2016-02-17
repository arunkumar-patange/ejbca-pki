#
# common
# NOTE: not to be use independtly
#

HIDE ?= @
SHELL := /bin/bash
REGISTRY ?= onelogin-docker-registry:5000

push:
	$(HIDE)docker tag -f $(DOCKER_IMAGE) $(REGISTRY)/$(DOCKER_IMAGE)
	$(HIDE)docker push $(REGISTRY)/$(DOCKER_IMAGE)
	$(HIDE)docker rmi $(REGISTRY)/$(DOCKER_IMAGE)
	$(HIDE)docker rmi $(DOCKER_IMAGE)

pull:
	$(HIDE)docker pull $(REGISTRY)/$(DOCKER_IMAGE)
	$(HIDE)docker tag -f $(REGISTRY)/$(DOCKER_IMAGE) $(DOCKER_IMAGE)

restart:
	$(HIDE)docker restart $(DOCKER_CONTAINER)


bases := docker
base-images:
	$(HIDE)$(foreach dir,$(bases),(pushd ../base/$(dir) && make image && popd) &&):
