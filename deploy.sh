#!/bin/bash

# setup config files
cp conf/ejbca/*.properties $EJBCA/conf/
mkdir -p $JBOSS/modules/org/postgresql/main/
cp conf/jboss/postgresql-9.1-903.jdbc4.jar  $JBOSS/modules/org/postgresql/main/
cp conf/jboss/module.xml $JBOSS/modules/org/postgresql/main/
cp conf/jboss/standalone.xml $JBOSS/standalone/configuration/

# start jboss
supervisord -c /ejbca/src/conf/supervisord.conf

# TODO: debug, comment it out
# psql -U postgres -hpostgres -c "DROP DATABASE ejbca"

# deploy
cd $EJBCA
ant clean
if [[ `psql -U postgres -hpostgres -tAc "SELECT 1 FROM pg_database WHERE datname='ejbca'"` == "1" ]]
then
    echo "re-deploy w/o db install"
    ant clean deployear
else
    # echo "Database does not exist"
    # CREATEUSER vs CREATE USER(ROLE)
    psql -U postgres -hpostgres -c "CREATE USER ejbca WITH PASSWORD 'ejbca';"
    psql -U postgres -hpostgres -c "CREATE DATABASE ejbca;"
    psql -U postgres -hpostgres -c "GRANT ALL PRIVILEGES ON DATABASE ejbca TO ejbca;"
    ant deploy
    ant install
fi
cd -

rm -rf $JBOSS/standalone/configuration/standalone_xml_history/current


#TODO: restart jboss supervisord
#supervisorctl reread
#supervisorctl update
