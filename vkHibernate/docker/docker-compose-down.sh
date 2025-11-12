#!/bin/bash
docker compose down --volumes
rm -r ./db/pgdata/*
rmdir ./db/pgdata
