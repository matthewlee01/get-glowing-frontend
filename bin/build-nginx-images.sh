#!/usr/bin/env bash
lein clean &&
lein cljsbuild once min &&
docker build -t archon .. 

