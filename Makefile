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
INAME ?= onelogin/ejbca
CNAME ?= ejbca


PHONY: postgresdb

image:
	$(HIDE)docker build -t $(INAME) .
	$(HIDE)docker run --name ant-deploy \
		--link postgres \
		-v $(PWD):/ejbca/src \
		$(INAME)
	$(HIDE)docker commit ant-deploy $(INAME):deploy
	$(HIDE)docker rm ant-deploy

start:
	$(HIDE)docker run -it --name $(CNAME) \
		--link postgres \
		-p 8080:8080 \
		-p 8443:8443 \
		-p 8442:8442 \
		-p 9990:9990 \
		-v $(PWD):/ejbca/src \
		-d $(INAME):deploy \
		/ejbca/src/run.sh

enter:
	#$(HIDE)docker attach $(CNAME)
	$(HIDE)docker exec -it $(CNAME) /bin/bash

stop:
	-$(HIDE)docker rm ant-deploy
	$(HIDE)docker stop $(CNAME)
	$(HIDE)docker rm $(CNAME)

postgresdb:
	$(HIDE)docker run --name postgres -e POSTGRES_PASSWORD=postgres -d postgres

sh:
	$(HIDE)eval $(docker env docker-gui)

clean-all:
	-$(HIDE)docker ps -aq | xargs docker stop
	-$(HIDE)docker ps -aq | xargs docker rm
	-$(HIDE)docker images -q| xargs docker rmi
