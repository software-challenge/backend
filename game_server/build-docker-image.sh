#!/bin/bash
temp_game_server_dir=game-server-build
if [ -e "$temp_game_server_dir" ]
then
  echo "We need to copy the game server build directory to ./$temp_game_server_dir, but it already exists. Exiting."
  exit 1
else
  ant clean && ant
  cp -r ../deploy/server "$temp_game_server_dir"
  TAG=$(git rev-parse --short --verify HEAD)
  docker build -t swc_game-server:latest -t swc_game-server:$TAG .
fi
