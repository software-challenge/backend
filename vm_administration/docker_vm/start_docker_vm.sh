#!/bin/bash
set -x # echo commands as they are executed

# Should get the path of the client zip file on the file host as first argument and optionally a
# path to a log file as second argument.

# The host we should the client zip get from:
CLIENT_ZIP_SSH_HOST=localhost
CLIENT_ZIP_SSH_PORT=2222
# The SSH key to authenticate to the host to get the client zip and the host where the client will run:
SSH_KEY=/home/svk/.ssh/socha_docker_vm
# the docker network the client container should run in to be able to connect to the game server
CLIENT_NETWORK=webapp_default
GAMESERVER_HOSTNAME=webapp_swc_game-server_1

# VM name will be unique as client vms get started in intervals 5 seconds
VMNAME='vmclient-'`/bin/date +%Y-%m-%d_%H-%M-%S`
DATEDIR=`/bin/date +%Y/%m/%d`
if [ -n "$2" ]; then
  VMLOG="$2"
else
  VMLOG="$HOME/log/vmclient/$DATEDIR/$VMNAME.log"
fi
# The whole path to the client zip is passed by the consumer to this script as first argument:
CLIENT_ZIP_PATH="$1"

# ----------------------------------------------------------------------
# main
#

mkdir -p $HOME/log/vmclient/$DATEDIR
(
/bin/echo "Starting a new VM at `/bin/date`"
/bin/echo "VM name: $VMNAME"

# ----------------------------------------------------------------------
# Check if we should use the new VMs
#
NEW_VM=false
if [[ $CLIENT_ZIP_PATH =~ .*_new.* ]]
then
  NEW_VM=true
fi

if [ "$NEW_VM" = true ]
then
  echo "We should use the new VM!"
else
  echo "We should use the old VM!"
fi

echo $CLIENT_ZIP_PATH

# Create and start new VM
echo "Starting vm $VMNAME"
CID=$(docker run -d --network $CLIENT_NETWORK --name $VMNAME docker_vm)

VMTIME=0
VMIP=""

# Get IP of started container
while [ -z $VMIP ]; do
  VMIP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' $CID)
  # only sleep if no IP could be obtained
  if [ -z $VMIP ]; then
    sleep 10
    VMTIME=$[$VMTIME+10]
  fi
  if [ $VMTIME -gt 180 ]; then
    echo "VM did not start correctly, no IP found after $VMTIME, terminating!"
    exit -1
  fi
done

echo "VM-IP found: $VMIP"

VMTIME=0
CHECK_INTERVAL=15
CLIENT_TIMEOUT=300

# 0 = initial, no vm started,
# 1 = VM was booted and accepts ssh connections,
# 2 = VM was booted and client was copied and started
VM_BOOTED=0

CONSUMER_SSH_PID=0

#SSH_OPTIONS="-q -o StrictHostKeyChecking=no -o BatchMode=true -o ConnectTimeout=5 -o UserKnownHostsFile=/dev/null -i $SSH_KEY"
SSH_OPTIONS="-o StrictHostKeyChecking=no -o BatchMode=true -o ConnectTimeout=5 -o UserKnownHostsFile=/dev/null -i $SSH_KEY"
echo "Waiting until timeout ($CLIENT_TIMEOUT seconds) reached or client terminated..."
while [[ $VMTIME -lt $CLIENT_TIMEOUT ]]; do

  if ([ $VM_BOOTED -eq 0 ]); then
    echo "VM not booted yet, trying to connect"
    ssh $SSH_OPTIONS root@$VMIP exit
    # the exit code of ssh is only 0 when a connection was successful
    if [ $? -eq 0 ]; then VM_BOOTED=1; fi
  fi
  if ([ $VM_BOOTED -eq 1 ]); then
    echo "VM booted, copying client file"
    scp $SSH_OPTIONS -P $CLIENT_ZIP_SSH_PORT root@$CLIENT_ZIP_SSH_HOST:"$CLIENT_ZIP_PATH" ./client.zip
    scp $SSH_OPTIONS ./client.zip root@$VMIP:/client/client.zip
    echo "Removing client zip from fileserver"
    #ssh $SSH_OPTIONS root@$CLIENT_ZIP_SSH_HOST:$CLIENT_ZIP_SSH_PORT rm "$CLIENT_ZIP_PATH"

    echo "Starting client..."
    ssh $SSH_OPTIONS root@$VMIP <<EOF
      cd /client
      unzip client.zip
      java -jar mississippi_queen_player.jar -h $GAMESERVER_HOSTNAME
EOF
    CONSUMER_SSH_PID=$!
    VM_BOOTED=2
  fi
  if ([ $VM_BOOTED -eq 2 ]); then
    echo "testing if ssh with consumer script (PID $CONSUMER_SSH_PID) is running"
    if ps -p $CONSUMER_SSH_PID > /dev/null; then
      echo "script is running, wait for it to stop"
    else
      echo "script is not running, we can finish"
      break
    fi
  fi
  echo "VM not ready or finished yet, waited $VMTIME, sleeping for $CHECK_INTERVAL"
  sleep $CHECK_INTERVAL
  VMTIME=$(($VMTIME+$CHECK_INTERVAL))
done

if [ $VMTIME -ge $CLIENT_TIMEOUT ]; then
  echo "Timeout reached! Shutting down! (This indicates that something went wrong!)"
fi

sleep 5

# ----------------------------------------------------------------------
# Copy the execution log from the VM to VMMain
#
echo "Saving log file"
docker logs $VMNAME

# ----------------------------------------------------------------------
# Kill the VM
#
if [ $CONSUMER_SSH_PID -ne 0 ]; then
  echo "Killing ssh command connected to VM (PID: $CONSUMER_SSH_PID)"
  kill $CONSUMER_SSH_PID
fi
docker stop $VMNAME
docker rm $VMNAME

echo "Finished"
)
#) >> $VMLOG 2>&1
#mv $VMLOG $HOME/log/vmclient/$DATEDIR/$VMNAME.log
#exit 0
