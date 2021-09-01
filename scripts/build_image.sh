#!/bin/zsh

LOCATION="$(dirname "$(pwd)")"

echo "----------- Building Docker Image ----------"
docker build -t rexijie/ukgovapi:0.1 -f ../docker/Dockerfile "$LOCATION"