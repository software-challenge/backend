#!/bin/bash

# WINE 1.0.1
# Java 1.6.15
# Perl 5.10
# Python 2.6.4

# log script starts
log="/home/scadmin/logs/exec.log"
/bin/echo "Running on `date`" > $log

# Setting up permissions
sudo /bin/chmod a+rw /dev/snd/seq >> $log

file=${scpcommand##*/}
clientname=${file%%.*}
resultarchive="/home/scadmin/${clientname}.tar"
zipfile="/home/clientexec/client/client.zip"
startup="/home/clientexec/client/startup.sh"
clientdir="/home/clientexec/client"

/bin/chown clientexec:clientexec $clientdir
/bin/chmod 777 $clientdir

echo "Unzipping client" >> $log
cd $clientdir
/usr/bin/unzip $zipfile >> $log 2>&1
/bin/chown -R clientexec:clientexec .
/bin/chmod +x $startup
echo "Starting client" >> $log
echo "-----------------------" >> $log
echo "" >> $log
sudo -Hu clientexec /bin/bash +x $startup >> $log 2>&1 --
echo "-----------------------" >> $log

cd /home/scadmin/

exit 0
