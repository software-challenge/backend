#!/bin/bash
# zfs list -r zpool1
echo "removing snapshot"
zfs destroy -R zpool1/vbox/harddisks/vmclient@kopie

echo "copying new .vdi"
cd ~/harddisks/vmclient
cp ~/harddisks/vm/vmclient.vdi .

echo "creating snapshot"
zfs snapshot zpool1/vbox/harddisks/vmclient@kopie

exit 0