#!/bin/bash

sbt universal:packageZipTarball
docker build -f modules/fetchers/homematic/docker/dev/Dockerfile -t razemio/metatron:dev .
docker push razemio/metatron:dev