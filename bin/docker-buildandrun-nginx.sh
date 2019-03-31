#!/usr/bin/env bash
docker build -t archon .. &&
docker run -tid --rm -p 80:80 archon

