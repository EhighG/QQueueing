#!/bin/bash

if [[ -z $1 ]];then
	echo "NO input error"
	exit 1;
fi
docker compose down $1
docker compose build $1
docker compose up $1
