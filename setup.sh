#!/bin/bash

cp conf/ejbca/*.properties $EJBCA/conf/

mkdir -p $JBOSS/modules/org/postgresql/main/
cp conf/jboss/postgresql-9.1-903.jdbc4.jar  $JBOSS/modules/org/postgresql/main/
cp conf/jboss/module.xml $JBOSS/modules/org/postgresql/main/
cp conf/jboss/standalone.xml $JBOSS/standalone/configuration/

# INFO: need connection to the database while setup
#supervisord -c /ejbca/src/supervisord.conf
#cd /ejbca/src/ejbca_ce_6_3_1_1
#ant deploy
#ant install
#cd -
