#!/bin/bash
SER="qqueueing-"
if [[ -z $1 ]];then
	echo "NO input error"
	exit 1;
fi

docker compose down $SER$1
docker compose build $SER$1

if [[ -z $2 ]];then
	echo "no second"
	docker compose up $SER$1
else
	docker compose up $2 $SER$1
fi
