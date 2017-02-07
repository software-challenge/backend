#!/bin/bash
ant clean && ant
TAG=$(git rev-parse --short --verify HEAD)
docker build --no-cache -t swc_producer:latest -t swc_producer:$TAG .
