#!/bin/bash

ROOT_DIR=$(git rev-parse --show-toplevel)
while true;do
	eval "$(cat $ROOT_DIR/src/pipes/pipe)" > $ROOT_DIR/src/pipes/log.txt 2>&1;
done
