#!/bin/bash
SER=""
DEFAULT_DIRECTORY=$(git rev-parse --show-toplevel)
COMPOSE_PATH=$DEFAULT_DIRECTORY/test/compose.yml
if [[ -z $1 ]];then
	echo "NO input error"
	exit 1;
fi

docker compose -f $COMPOSE_PATH down $SER$1
docker compose -f $COMPOSE_PATH build $SER$1

if [[ -z $2 ]];then
	echo "no second"
	docker compose -f $COMPOSE_PATH up $SER$1
else
	docker compose -f $COMPOSE_PATH up $2 $SER$1
fi
