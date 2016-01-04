#!/bin/bash

supervisord -c /ejbca/src/conf/supervisord.conf
while true; do sleep 60; done
