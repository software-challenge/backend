#!/bin/bash

# ----------------------------------------------------------------------
# stopVM.sh
#
# Stoppt eine laufende VMClient und loescht den dazugehoerigen HDD Klon.
#
# (c) 2009-2015 Software Challenge
#
# HISTORY
#
# 01.06.2015 svk Nachtraegliches Loeschen des VM-Verzeichnisses eingebaut
# 17.06.2012 sca Keine ZFS-Clones mehr benutzen, sondern VirtualBox Snapshots und differencing VDI images
# 27.01.2011 sca Historie begonnen
#

# ----------------------------------------------------------------------
# globals
#

VMNAME=$1

# ----------------------------------------------------------------------
# main
#

echo "Removing vm $VMNAME"
echo " - shutting down..."
VBoxManage controlvm $VMNAME poweroff
sleep 2
echo " - unregister & delete..."
VBoxManage unregistervm $VMNAME --delete
VM_DIR="/home/vbox/vms/$VMNAME"
if [ -d "$VM_DIR" ]; then
    # cloned virtual machines using the new VM image are not deleted
    # properly by unregister --delete. The directory still contains a
    # Snapshot directory with one .sav file in it.
    echo "directory $VM_DIR still exists, deleting..."
    rm -rf "$VM_DIR"
fi



# ----------------------------------------------------------------------
# end of file
# ----------------------------------------------------------------------
