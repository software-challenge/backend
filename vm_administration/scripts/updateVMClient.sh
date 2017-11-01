#!/bin/bash

# ----------------------------------------------------------------------
# updateVMClient.sh
#
# Kopiert die Originalvorlage der VMClient und erstellt davon einen ZFS-Snapshot.
# LÃoescht ggf. den alten SnapshotL Ã
#
# (c) 2009-2011 software-challenge
#
# HISTORY
#
# 27.01.2011 wib historie begonnen
#

# ----------------------------------------------------------------------
# globals
#

# ----------------------------------------------------------------------
# main
#
SNAP=`zfs list -t snapshot -o name | egrep runclient`
if [ -n "$SNAP" ]; then
  echo "removing snapshot"
  zfs destroy -R zpool1/vbox/vms/runclient@kopie
fi

echo "copying new .vdi"
cd $HOME/vms/runclient
cp $HOME/vms/vmclient/*.vdi .

echo "creating snapshot"
zfs snapshot zpool1/vbox/vms/runclient@kopie

exit 0

# ----------------------------------------------------------------------
# end of file
# ----------------------------------------------------------------------

