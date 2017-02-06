#!/bin/bash

# Should get the name of the client zip file as first argument and optionally a
# path to a log file as second argument.

CLIENT_ZIP_SFTP_HOST=localhost
CLIENT_ZIP_SFTP_PORT=2222
CLIENT_ZIP_SFTP_USER=swc
CLIENT_ZIP_SFTP_PASS=secret
SSH_KEY=/home/svk/.ssh/socha_docker_vm

# VM name will be unique as client vms get started in intervals 5 seconds
VMNAME='vmclient-'`/bin/date +%Y-%m-%d_%H-%M-%S`
DATEDIR=`/bin/date +%Y/%m/%d`
if [ -n "$2" ]; then
  VMLOG="$2"
else
  VMLOG="$HOME/log/vmclient/$DATEDIR/$VMNAME.log"
fi
CLIENT_ZIP="$1"

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
if [[ $CLIENT_ZIP =~ .*_new.* ]]
then
  NEW_VM=true
fi

if [ "$NEW_VM" = true ]
then
  echo "We should use the new VM!"
else
  echo "We should use the old VM!"
fi

echo $CLIENT_ZIP

# Create and start new VM
echo "Starting vm $VMNAME"
CID=$(docker run -d --name $VMNAME docker_vm)

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

#SSH_OPTIONS="-q -o StrictHostKeyChecking=no -o BatchMode=true -o ConnectTimeout=5 -o UserKnownHostsFile=/dev/null -i $SSH_KEY -l root"
SSH_OPTIONS="-v -o StrictHostKeyChecking=no -o BatchMode=true -o ConnectTimeout=5 -o UserKnownHostsFile=/dev/null -i $SSH_KEY -l root"
echo "Waiting until timeout ($CLIENT_TIMEOUT seconds) reached or client terminated..."
while [[ $VMTIME -lt $CLIENT_TIMEOUT ]]; do

  if ([ $VM_BOOTED -eq 0 ]); then
    echo "VM not booted yet, trying to connect"
    ssh $SSH_OPTIONS $VMIP exit
    # the exit code of ssh is only 0 when a connection was successful
    if [ $? -eq 0 ]; then VM_BOOTED=1; fi
  fi
  if ([ $VM_BOOTED -eq 1 ]); then
    echo "VM booted, copying client file"
    echo "executing scp -p $CLIENT_ZIP_SFTP_PORT $CLIENT_ZIP_SFTP_USER@$CLIENT_ZIP_SFTP_HOST:$CLIENT_ZIP root@$VMIP:/app/client.zip..."
    scp -i $SSH_KEY -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -p $CLIENT_ZIP_SFTP_PORT $CLIENT_ZIP_SFTP_USER@$CLIENT_ZIP_SFTP_HOST:"$CLIENT_ZIP" root@$VMIP:/client/client.zip
    echo "executing ssh -l scadmin 192.168.56.2 rm $CLIENT_ZIP..."
    # TODO ssh $SSH_OPTIONS 192.168.56.2 rm "$CLIENT_ZIP"

    echo "Starting client..."
    ssh -i $SSH_KEY -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no root@$VMIP /bin/bash -c "cd /client && unzip client.zip && java -jar mississippi_queen_player.jar" &
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

exit -1
# ----------------------------------------------------------------------
# Copy the execution log from the VM to VMMain
#
echo "Saving log file"

if [ -n $VMIP ]
then
    TRIES=0
    while [[ $TRIES -lt 5 ]]; do
        echo "Copying from $VMIP"
        `ssh -q -l scadmin 134.245.253.5 ./getLog.sh $VMIP $VMNAME`
        if [ $? -eq 0 ]; then
            echo "Successfully copied log"
            break
        fi
        # this did not happen for a long time, but will leave it here
        TRIES=$(($TRIES+1))
        echo "Error copying log, try again $TRIES/5 in 5 seconds"
        sleep 5
        VMIP=`VBoxManage guestproperty get $VMNAME /VirtualBox/GuestInfo/Net/0/V4/IP | grep 'Value:' | sed 's/Value: \([0-9.]*\).*/\1/;q'`
    done
else
    echo "no ip found for this vm"
fi

# ----------------------------------------------------------------------
# Kill the VM
#

if [ $CONSUMER_SSH_PID -ne 0 ]; then
  echo "Killing ssh command connected to VM (PID: $CONSUMER_SSH_PID)"
  kill $CONSUMER_SSH_PID
fi
$HOME/bin/stopVM.sh $VMNAME

echo "Finished"
)
#) >> $VMLOG 2>&1
#mv $VMLOG $HOME/log/vmclient/$DATEDIR/$VMNAME.log
#exit 0
