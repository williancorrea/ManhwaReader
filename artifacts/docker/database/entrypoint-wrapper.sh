#!/bin/bash

# Run your startup script
/app/database.sh &

# Executes the default MariaDB entrypoint with the arguments
exec docker-entrypoint.sh "$@"
