#!/bin/bash
cd ../../consumer
sh gradlew clean bootJar
cp build/libs/*.jar ../build/consumer
