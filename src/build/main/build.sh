#!/bin/bash
cd ../../main
sh gradlew clean bootJar
cp build/libs/*.jar ../build/main
