#!/bin/bash

# ----------------------------------------------------------------------
# startVM.sh
#
# Erstellt einen Klon der VMClient und fuehrt diesen 5 Minuten lang aus.
# Danach loescht es den Klon wieder.
#
# ----------------------------------------------------------------------

# VM name will be unique as client vms get started in intervals 5 seconds
VMNAME='vmclient-'`/bin/date +%Y-%m-%d_%H-%M-%S`
DATEDIR=`/bin/date +%Y/%m/%d`
if [ -n "$2" ]; then
  VMLOG="$2"
else
  VMLOG="$HOME/log/vmclient/$DATEDIR/$VMNAME.log"
fi
CLIENT_ZIP="$1"

VMMAIN=192.168.56.2

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
  echo $CLIENT_ZIP
  echo "Creating vm clone"
  VBoxManage clonevm vmclient14.04 --snapshot snap6 --options link --name $VMNAME --register
else
  echo "We should use the old VM!"
  echo $CLIENT_ZIP
  echo "Creating vm clone"
  VBoxManage clonevm vmclient --snapshot snap8 --options link --name $VMNAME --register
fi

# Create and start new VM
echo "Starting vm $VMNAME"
VBoxManage startvm $VMNAME --type headless

VMTIME=0
VMIP=""

while [ -z $VMIP ]; do
  VMIP=`VBoxManage guestproperty get $VMNAME /VirtualBox/GuestInfo/Net/0/V4/IP | grep 'Value:' | sed 's/Value: \([0-9.]*\).*/\1/;q'`
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

SSH_KEY=/home/vbox/.ssh/id_rsa
# NOTE that -l is illegal for scp, don't specify it here. All options have to be valid for scp and ssh!
SSH_OPTIONS="-q -o StrictHostKeyChecking=no -o BatchMode=true -o ConnectTimeout=5 -o UserKnownHostsFile=/dev/null -i $SSH_KEY"
echo "Waiting until timeout ($CLIENT_TIMEOUT seconds) reached or client terminated..."
while [[ $VMTIME -lt $CLIENT_TIMEOUT ]]; do

  VMIPNEW=`VBoxManage guestproperty get $VMNAME /VirtualBox/GuestInfo/Net/0/V4/IP | grep 'Value:' | sed 's/Value: \([0-9.]*\).*/\1/;q'`
  if [ "$VMIPNEW" != "$VMIP" ]; then
    # guestproperty may return a wrong ip when the VM is not fully
    # booted this might be a problem when the already retrieved IP is
    # now assigned to another machine and some operations of this
    # script already used it (e.g. to test if connection via ssh is
    # possible)
    echo "VM IP changed from $VMIP to $VMIPNEW. Using new IP"
    VMIP=$VMIPNEW
  fi

  if ([ $VM_BOOTED -eq 0 ]); then
    echo "VM not booted yet, trying to connect"
    ssh $SSH_OPTIONS scadmin@$VMIP exit
    # the exit code of ssh is only 0 when a connection was successful
    if [ $? -eq 0 ]; then VM_BOOTED=1; fi
  fi
  if ([ $VM_BOOTED -eq 1 ]); then
    echo "VM booted, copying client file"
    set -x # echo commands as they are executed
    TEMP_ZIP_NAME=client-for-$VMIP.zip
    scp $SSH_OPTIONS scadmin@$VMMAIN:"$CLIENT_ZIP" ./$TEMP_ZIP_NAME
    scp $SSH_OPTIONS ./$TEMP_ZIP_NAME scadmin@$VMIP:/home/clientexec/client/client.zip
    ssh $SSH_OPTIONS scadmin@$VMMAIN rm "$CLIENT_ZIP"
    echo "Starting client..."
    ssh $SSH_OPTIONS scadmin@$VMIP sudo /bin/bash /home/scadmin/consume.sh &
    set +x # no more echo commands as they are executed
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

if [ -n $VMIP ]
then
    TRIES=0
    while [[ $TRIES -lt 5 ]]; do
        echo "Copying from $VMIP"
        ssh $SSH_OPTIONS scadmin@$VMMAIN ./getLog.sh $VMIP $VMNAME
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
) >> $VMLOG 2>&1
mv $VMLOG $HOME/log/vmclient/$DATEDIR/$VMNAME.log
exit 0
