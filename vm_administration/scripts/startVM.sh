#!/bin/bash

message=$1
log="/home/vbox/logs/script.log"
/bin/echo "Starting a new VM at `/bin/date`" >> $log

vmnr=`/bin/date +%m%d%H%M%S`
zfspath="zpool1/vbox/harddisks/vmclient-$vmnr"
vmpath="/home/vbox/harddisks/vmclient-$vmnr"

echo "creating vdi clone, setting uuid"
/bin/echo "Cloning current vmclient" >> $log
zfs clone zpool1/vbox/harddisks/vmclient@kopie $zfspath
/bin/echo "Changing to new vdi path" >> $log
cd $vmpath
VBoxManage internalcommands sethduuid vmclient.vdi
cd

/bin/echo "creating vm"
vmname="vmclient-$vmnr"
VBoxManage createvm -name $vmname -register --ostype Ubuntu
VBoxManage modifyvm $vmname --memory 1536 --cpus 1
VBoxManage modifyvm $vmname --nic1 hostonly --nictype1 82540EM --cableconnected1 on --hostonlyadapter1 vboxnet0 --macaddress1 auto
VBoxManage modifyvm $vmname --hda "$vmpath/vmclient.vdi"

/bin/echo "starting vm"
VBoxManage startvm $vmname --type headless

/bin/echo "5"
sleep 60
/bin/echo "4"
sleep 60
/bin/echo "3"
sleep 60
/bin/echo "2"
sleep 60
/bin/echo "1"
sleep 60
/bin/echo "0"

echo "removing vm" >> $log
VBoxManage controlvm $vmname poweroff
VBoxManage modifyvm $vmname --hda none
VBoxManage unregistervm $vmname

zfs destroy -f $zfspath

vboxxml="/home/vbox/.VirtualBox/VirtualBox.xml"
vboxxmlb="/home/vbox/.VirtualBox/VirtualBox.xml.back"
sed -e "s/<HardDisk uuid=\"{[a-zA-Z0-9\-]*}\" location=\"\/home\/vbox\/harddisks\/vmclient-[0-9]*\/vmclient.vdi\" format=\"VDI\" type=\"Normal\"\/>//g" $vboxxml > $vboxxmlb
/bin/rm $vboxxml
/bin/cp $vboxxmlb $vboxxml

#a=`/bin/ps -fu vbox | /bin/grep $vmname`
#a=`ps -fu vbox | grep vmclient`
#/bin/echo $a
#b=`/bin/echo $a | /bin/sed "s/vbox //"`
# b=`/bin/echo $a | /bin/sed "s/vbox [0-9]* [0-9]* [0-9]* [A-Za-z0-9\ ]* \?//"`
#/bin/echo $b
#c=${b%% *}
# c=${b%%:*}
#/bin/echo $c
# if [$c gt 0]
#   /bin/kill $d 
# fi
#/bin/kill $c

exit 0