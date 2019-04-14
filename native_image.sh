#!/usr/bin/env sh

./gradlew clean shadowJar
unzip build/libs/task-scheduler*.jar -d build/libs/task-scheduler
native-image -H:ReflectionConfigurationFiles=graal/app.json -Dfile.encoding=UTF-8 -cp "./build/libs/task-scheduler" cs.put.ptsz.taskscheduler.Benchmark
