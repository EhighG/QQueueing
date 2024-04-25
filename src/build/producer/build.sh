#!/bin/bash
cd ../../producer
sh gradlew clean bootJar
cp build/libs/*.jar ../build/producer
