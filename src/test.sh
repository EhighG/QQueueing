#!/bin/bash
SER="qqueueing-"
DEFAULT_DIRECTORY=$(git rev-parse --show-toplevel)
COMPOSE_PATH=$DEFAULT_DIRECTORY/src/compose.yml

if [[ -z $1 ]];then
	echo "NO input error"
	exit 1;
fi

docker compose --env-file ../.env -f $COMPOSE_PATH down $SER$1
docker compose --env-file ../.env -f $COMPOSE_PATH build $SER$1

if [[ -z $2 ]];then
	echo "no second"
	docker compose --env-file ../.env -f $COMPOSE_PATH up $SER$1
else
	docker compose --env-file ../.env -f $COMPOSE_PATH up $2 $SER$1
fi
