#!/bin/bash

# CREATEUSER vs CREATE USER(ROLE)
psql -U postgres -c "DROP DATABASE ejbca"
psql -U postgres -c "CREATE USER ejbca WITH PASSWORD 'ejbca';"
psql -U postgres -c "CREATE DATABASE ejbca;"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE ejbca TO ejbca;"
