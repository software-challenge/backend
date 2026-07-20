#!/bin/sh
echo "pushing to staging"
server=ebbe "$(dirname $0)/push_to_server.sh"
