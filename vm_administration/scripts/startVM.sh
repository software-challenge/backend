#!/bin/bash

# ----------------------------------------------------------------------
# startVM.sh
#
# Erstellt einen Klon der VMClient und fuehrt diesen 5 Minuten lang aus.
# Danach loescht es den Klon wieder.
#
# ----------------------------------------------------------------------

# VM name will be unique as client vms get started in intervals 5 seconds
VMNAME='vmclient-'$(/bin/date +%Y-%m-%d_%H-%M-%S)
DATEDIR=$(/bin/date +%Y/%m/%d)
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

mkdir -p "$HOME/log/vmclient/$DATEDIR"
(
/bin/echo "Starting a new VM at $(/bin/date)"
/bin/echo "VM name: $VMNAME"

# ----------------------------------------------------------------------
# Check if we should use the new VMs
#
NEW_VM=false
if [[ $CLIENT_ZIP =~ .*_new.* ]]
then
  NEW_VM=true
fi

# 0 = initial, no vm started,
# 1 = VM was booted and accepts ssh connections,
# 2 = VM was booted and client was copied and started
VM_BOOTED=0

# Create and start new VM
echo "Starting vm $VMNAME"
VBoxManage startvm $VMNAME --type headless

    if [[ $VMSTARTTRIES -gt 1 ]]
    then
        VMNAME=$VMNAME-$VMSTARTTRIES
    fi

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
    VMIP=""

# 0 = initial, no vm started,
# 1 = VM was booted and accepts ssh connections,
# 2 = VM was booted and client was copied and started
VM_BOOTED=0

    echo "VM-IP found: $VMIP"

SSH_OPTIONS="-q -o StrictHostKeyChecking=no -o BatchMode=true -o ConnectTimeout=5 -o UserKnownHostsFile=/dev/null -l scadmin"
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
    ssh $SSH_OPTIONS $VMIP exit
    # the exit code of ssh is only 0 when a connection was successful
    if [ $? -eq 0 ]; then VM_BOOTED=1; fi
  fi
  if ([ $VM_BOOTED -eq 1 ]); then
    echo "VM booted, copying client file"
    echo "executing scp scadmin@192.168.56.2:$CLIENT_ZIP scadmin@$VMIP:/home/clientexec/client/client.zip..."
    ssh scadmin@192.168.56.2 scp -i /home/scadmin/.ssh/client_key -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no "$CLIENT_ZIP" scadmin@$VMIP:/home/clientexec/client/client.zip
    echo "executing ssh -l scadmin 192.168.56.2 rm $CLIENT_ZIP..."
    ssh $SSH_OPTIONS 192.168.56.2 rm "$CLIENT_ZIP"
    echo "Starting client..."
    ssh -i /home/vbox/.ssh/id_rsa -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no scadmin@$VMIP sudo /bin/bash /home/scadmin/consume.sh &
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

if [ $VMTIME -ge $CLIENT_TIMEOUT ]; then
  echo "Timeout reached! Shutting down! (This indicates that something went wrong!)"
fi

sleep 5

# ----------------------------------------------------------------------
# Copy the execution log from the VM to VMMain
#
echo "Saving log file"

if [ -n "$VMIP" ]
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
        TRIES=$((TRIES+1))
        echo "Error copying log, try again $TRIES/5 in 5 seconds"
        sleep 5
        VMIP=$(VBoxManage guestproperty get $VMNAME /VirtualBox/GuestInfo/Net/0/V4/IP | grep 'Value:' | sed 's/Value: \([0-9.]*\).*/\1/;q')
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
