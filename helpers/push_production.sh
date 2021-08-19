#!/bin/bash

echo "pushing to PRODUCTION"
export server=flut
./helpers/push_to_server.sh
