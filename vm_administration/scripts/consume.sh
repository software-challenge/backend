#!/bin/bash

# WINE 1.0.1
# Java 1.6.15
# Perl 5.10
# Python 2.6.4

# log script starts
log="/home/scadmin/logs/exec.log"
/bin/echo "Running on `date`" > $log

scpcommand=$1
file=${scpcommand##*/}
clientname=${file%%.*}
resultarchive="/home/scadmin/${clientname}.tar"
zipfile="/home/scadmin/client/client.zip"
startup="/home/scadmin/client/startup.sh"
clientdir="/home/scadmin/client"

echo "$scpcommand $zipfile" >> $log
`$scpcommand $zipfile` >> $log 2>&1

/bin/mkdir $clientdir
/bin/mv $zipfile $clientdir
cd $clientdir
/usr/bin/unzip $zipfile >> $log 2>&1
/bin/chmod +x $startup
/bin/bash +x $startup >> $log 2>&1

cd /home/scadmin/

/bin/tar -czf $resultarchive client logs
/bin/scp -i /home/scadmin/id_rsa $resultarchive scadmin@192.168.56.2:/home/scadmin/out/

exit 0