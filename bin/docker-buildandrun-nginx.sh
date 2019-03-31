#!/usr/bin/env bash
lein cljsbuild once min &&
docker build -t archon .. &&
docker run -tid --rm -p 80:80 archon

