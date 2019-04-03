#!/usr/bin/env bash
lein cljsbuild once min &&
docker build -t archon .. 

