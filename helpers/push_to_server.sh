#!/bin/sh -ex

if test -z "$server"; then
    # server is not set or ""
    echo "please call push_staging.sh or push_production.sh"
    exit 1
fi

if test -S .ssh-socket~
then ssh -S .ssh-socket~ -O exit $server && sleep 1 || rm .ssh-socket~
fi

ssh -M -S .ssh-socket~ -fnNT -L 5000:localhost:5000 $server
ssh -S .ssh-socket~ -O check $server || exit 1
echo testpassword | docker login --username testuser --password-stdin localhost:5000 || exit 1

./gradlew clean dockerImage

docker tag swc_game-server localhost:5000/swc_game-server
docker push localhost:5000/swc_game-server

ssh -S .ssh-socket~ -O exit $server

ssh $server 'sudo docker pull localhost:5000/swc_game-server' && \
# NOTE This requires the other services (client-controller and gameserver) in the compose file also already deployed
ssh $server 'sudo docker service update --image localhost:5000/swc_game-server:latest --with-registry-auth contest_gameserver'
