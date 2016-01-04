#
# Makefile
# postgres
#

SHELL := /bin/bash
HIDE ?= @
INAME ?= onelogin/postgres
CNAME ?= postgres

image:
	$(HIDE)docker build -t $(INAME) .

start:
	$(HIDE)docker run -it --name $(CNAME) \
		-e POSTGRES_PASSWORD=postgres \
		-d $(INAME) \
		/bin/bash
		# ./run.sh

server:
	$(HIDE)docker exec -it $(CNAME) s

enter:
	$(HIDE)docker exec -it $(CNAME) /bin/bash

stop:
	$(HIDE)docker stop $(CNAME)
	$(HIDE)docker rm $(CNAME)
