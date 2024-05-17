#!/bin/bash
SER="qqueueing-"
DEFAULT_DIRECTORY=$(git rev-parse --show-toplevel)
COMPOSE_PATH=$DEFAULT_DIRECTORY/src/compose.yml

if [[ -z $1 ]];then
	echo "NO input error"
	exit 1;
fi

if [[ $1 == "test" ]];then
	docker compose --env-file ../.env -f $COMPOSE_PATH down $1
	docker compose --env-file ../.env -f $COMPOSE_PATH build $1
	if [[ -z $2 ]];then
		echo "no second"
		docker compose --env-file ../.env -f $COMPOSE_PATH up $1
	else
		docker compose --env-file ../.env -f $COMPOSE_PATH up $2 $1
	fi
	exit 0 
fi
docker compose -f $COMPOSE_PATH down $SER$1
docker compose -f $COMPOSE_PATH build $SER$1

if [[ -z $2 ]];then
	echo "no second"
	docker compose -f $COMPOSE_PATH up $SER$1
else
	docker compose -f $COMPOSE_PATH up $2 $SER$1
fi
