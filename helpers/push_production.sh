#!/bin/sh
echo "pushing to PRODUCTION"
server=flut "$(dirname $0)/push_to_server.sh"
