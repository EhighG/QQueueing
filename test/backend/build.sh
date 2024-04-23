#!/bin/bash
cd tes24
sh gradlew clean bootJar
cp build/libs/*.jar ..
