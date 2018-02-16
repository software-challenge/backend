#!/bin/bash
temp_game_server_dir=game-server-build-$RANDOM
if [ -e "$temp_game_server_dir" ]
then
  echo "We need to copy the game server build directory to ./$temp_game_server_dir, but it already exists. Exiting."
  exit 1
else
  cp -r ../build/software-challenge-server/runnable "$temp_game_server_dir"
  TAG=$(git rev-parse --short --verify HEAD)
  docker build --no-cache -t swc_game-server:latest -t swc_game-server:$TAG --build-arg game_server_dir=$temp_game_server_dir .
  rm -rf "$temp_game_server_dir"
fi
