#!/bin/bash
echo "Killing the running VMs will leave undeleted remains behind. These may include semaphores, the HDD zfs clone, the HDD registry entry from VirtualBox and the machine's registry entry from VirtualBox. These will have to be cleaned up manually."
read -n1 -p "Do you want to continue? [y]es, [N]o: " answer
echo
case $answer in
	y | Y | yes | Yes)
		ps -fu vbox | grep startVM.sh | awk '{print $2}' | xargs kill -9
		ps -fu vbox | grep sleep | awk '{print $2}' | xargs kill -9 
		ps -fu vbox | grep vmclient | awk '{print $2}' | xargs kill
	;;
	* )
	;;
esac
