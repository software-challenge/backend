#!/bin/bash

# -------------------------------------------
# start_vmmain.sh
# 
# Starts the VM Main in headless mode 
#
# -------------------------------------------

VBoxManage startvm vmmain_1 --type headless
echo "Started VMMain system should be running soon"
echo "VMs running:"
VBoxManage list runningvms
