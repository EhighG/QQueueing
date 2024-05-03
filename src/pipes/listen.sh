#!/bin/bash


while true;do
	eval "$(cat pipe)" > ./log.txt 2>&1;
done
