#!/bin/bash

# set -a
# source .env

BASEDIR=$(dirname $(realpath "$0"))

docker-compose -f "${BASEDIR}/docker-compose.yml" up -d

