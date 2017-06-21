#!/bin/sh

# Kills all startVM.sh processes.
# The startVM.sh script tries indefinitely when the IP of the started VM cannot be retrieved.
# In the case that no more IPs are supplied by the virtualbox DHCP server, the scripts need to be killed.

ps -Af | grep startVM.sh | grep -v grep | awk '{print $2}' | xargs kill
