#!/bin/bash

# ----------------------------------------------------------------------
# startVM.sh
#
# Erstellt einen Klon der VMClient und fuehrt diesen 5 Minuten lang aus.
# Danach loescht es den Klon wieder.
#
# (c) 2009-2011 software-challenge
#
# HISTORY
#
# 12.01.2016 svk Timeout fuer SSH-Aufrufe
# 02.03.2015 fdu Support für die neuen ClientVMs
# 06.03.2014 svk SSH-Prozess der das consumer.sh Skript startet wird vor beenden der VM gekillt
# 17.06.2012 sca Keine ZFS-Clones mehr benutzen, sondern VirtualBox Snapshots und differencing VDI images
# 17.01.2012 sca Korrekte Benennung der VM-Logfiles
# 14.01.2012 sca Neustart der VM, wenn sie nich konsumiert
# 27.01.2011 sca Historie begonnen
#

# ----------------------------------------------------------------------
# globals
#

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
  echo $CLIENT_ZIP
  echo "Creating vm clone"
  VBoxManage clonevm vmclient14.04 --snapshot snap5 --options link --name $VMNAME --register
else
  echo "We should use the old VM!"
  echo $CLIENT_ZIP
  echo "Creating vm clone"
  VBoxManage clonevm vmclient --snapshot snap8 --options link --name $VMNAME --register
fi

# Create and start new VM
echo "Starting vm $VMNAME"
VBoxManage startvm $VMNAME --type headless

# ----------------------------------------------------------------------
# Getting the VM-IP
#
VMTIME=0
VMIP=""
if [ "$NEW_VM" = true ]
then
  # new vm
  while [ -z $VMIP ]; do
    VMIP=$(VBoxManage guestcontrol $VMNAME execute --image "/home/scadmin/getIP.sh" --username scadmin --password scadmin --wait-stdout)
    sleep 10
    VMTIME=$[$VMTIME+10]
    if [ $VMTIME -gt 180 ]; then
      echo "VM did not start correctly, no IP found after $VMTIME, starting new VM!"
      $HOME/bin/stopVM.sh $VMNAME
      nohup $HOME/bin/startVM.sh $CLIENT_ZIP $VMLOG &
      exit 0
    fi
  done
else
  #old vm
  while [ -z $VMIP ]; do
   VMIP=`VBoxManage guestproperty get $VMNAME /VirtualBox/GuestInfo/Net/0/V4/IP | grep 'Value:' | sed 's/Value: \([0-9.]*\).*/\1/;q'`
   sleep 10
   VMTIME=$[$VMTIME+10]
   if [ $VMTIME -gt 180 ]; then
    echo "VM did not start correctly, no IP found after $VMTIME, starting new VM!"
    $HOME/bin/stopVM.sh $VMNAME
    nohup $HOME/bin/startVM.sh $CLIENT_ZIP $VMLOG &
    exit 0
   fi
  done
fi

echo "VM-IP found: $VMIP"

#----------------------------------------------------------------------
# Waiting until the Client is started and no Client-Process found!
#

VMTIME=0
CHECK_INTERVAL=15
CLIENT_TIMEOUT=300
VM_BOOTED="0"
CONSUMER_SSH_PID=0
PING_PID=0

echo "Waiting until timeout reached or client terminated..."
while [[ $VMTIME -lt $CLIENT_TIMEOUT ]]; do
  if [ "$NEW_VM" = true ]
    then
      VMIPNEW=$(VBoxManage guestcontrol $VMNAME execute --image "/home/scadmin/getIP.sh" --username scadmin --password scadmin --wait-stdout)
    else
      VMIPNEW=`VBoxManage guestproperty get $VMNAME /VirtualBox/GuestInfo/Net/0/V4/IP | grep 'Value:' | sed 's/Value: \([0-9.]*\).*/\1/;q'`
  fi

  if [ "$VMIPNEW" != "$VMIP" ]; then
    echo "VM IP changed from $VMIP to $VMIPNEW. This is NOT GOOD!"
  fi
  CLIENT_PROCS=`$HOME/bin/timeout.sh ssh -q -o StrictHostKeyChecking=no -l scadmin $VMIP ps -u clientexec | wc -l`
  CLIENT_STARTED=`$HOME/bin/timeout.sh ssh -q -o StrictHostKeyChecking=no -l scadmin $VMIP ls /home/clientexec/ | grep started | wc -l`
  if ([ $VM_BOOTED == "0" ]); then
    VM_BOOTED=`$HOME/bin/timeout.sh ssh -q -o StrictHostKeyChecking=no -l scadmin $VMIP ls /home/scadmin/ | grep booted | wc -l`
    echo "VM not booted yet: $VM_BOOTED"
  fi
  if ([ $VM_BOOTED == "1" ]); then
    echo "VM booted, copying client file"
    echo "executing scp scadmin@192.168.56.2:$CLIENT_ZIP scadmin@$VMIP:/home/clientexec/client/client.zip..."
    ssh scadmin@192.168.56.2 scp -i /home/scadmin/.ssh/client_key -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no $CLIENT_ZIP scadmin@$VMIP:/home/clientexec/client/client.zip
    echo "executing ssh -l scadmin 192.168.56.2 rm $CLIENT_ZIP..."
    ssh -o StrictHostKeyChecking=no -l scadmin 192.168.56.2 rm $CLIENT_ZIP
    echo "Starting client..."
    ssh -i /home/vbox/.ssh/id_rsa -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -o StrictHostKeyChecking=no scadmin@$VMIP sudo /bin/bash /home/scadmin/consume.sh &
    CONSUMER_SSH_PID=$!
    ping -s $VMIP > $VMLOG.ping.log &
    PING_PID=$!
    VM_BOOTED="2"
  fi
  if ([ $CLIENT_STARTED == "1" ]&&[ $CLIENT_PROCS -lt 2 ]);  then
    # this is the normal case and should be reached after the client has terminated
    echo "Client was started and no client-processes were found. Therefore shutting down!"
    break
  fi
  #if ([ $CLIENT_STARTED == "0" ] && [ $VMTIME -gt 60 ]); then
  #  echo "VM did start but not consume anything after $VMTIME seconds. Starting new VM!"
  #  $HOME/bin/stopVM.sh $VMNAME
  #  nohup $HOME/bin/startVM.sh $CLIENT_ZIP $VMLOG &
  #  exit 0
  #fi
  echo "VM not ready yet, waited $VMTIME, sleeping for $CHECK_INTERVAL"
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
if [ "$NEW_VM" = true ]
then
  VMIP=$(VBoxManage guestcontrol $VMNAME execute --image "/home/scadmin/getIP.sh" --username scadmin --password scadmin --wait-stdout)
else
  VMIP=`VBoxManage guestproperty get $VMNAME /VirtualBox/GuestInfo/Net/0/V4/IP | grep 'Value:' | sed 's/Value: \([0-9.]*\).*/\1/;q'`
fi

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
                TRIES=$(($TRIES+1))
                echo "Error copying log, try again $TRIES/5 in 5 seconds"
                sleep 5
                if [ "$NEW_VM" = true ]
                then
                  VMIP=$(VBoxManage guestcontrol $VMNAME execute --image "/home/scadmin/getIP.sh" --username scadmin --password scadmin --wait-stdout)
                else
                  VMIP=`VBoxManage guestproperty get $VMNAME /VirtualBox/GuestInfo/Net/0/V4/IP | grep 'Value:' | sed 's/Value: \([0-9.]*\).*/\1/;q'`
                fi
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
  kill $PING_PID
fi
$HOME/bin/stopVM.sh $VMNAME

echo "Finished"
) >> $VMLOG 2>&1
mv $VMLOG $HOME/log/vmclient/$DATEDIR/$VMNAME.log
exit 0

# ----------------------------------------------------------------------
# end of file
# ----------------------------------------------------------------------
