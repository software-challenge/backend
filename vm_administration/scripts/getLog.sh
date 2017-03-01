#!/bin/sh

# This script is called by numerobis on the home of the VMMAIN

# Log actions?
log="/home/scadmin/getLog.log"
#log="/dev/null" # Do not log

MAX_FILE_SIZE_BYTES=5242880

echo `date` >> $log
echo "Copying log file" >> $log
echo "Max file size: ${MAX_FILE_SIZE_BYTES}" >> $log
echo "IP: $1" >> $log
echo "VMName: $2" >> $log
ssh -l scadmin -i "$HOME/.ssh/client_key" $1 "head -c ${MAX_FILE_SIZE_BYTES} ~/logs/exec.log" > ~/clientlogs/$2.log

#client_id=`cat ~/clientlogs/$2.log | grep '192.168.56.2:/home/scadmin/tmp/' | sed 's/.*192\.168\.56\.2:\/home\/scadmin\/tmp\/[0-9]*_[0-9]*_.*_\([0-9]*\)\.zip.*/\1/;q'`
client_id=`grep "Client ID: " clientlogs/$2.log | sed 's/Client ID: \([0-9]*\)/\1/;q'`
echo "ClientID: $client_id" >> $log
logfile_dir=`cat ~/clientlogs/$2.log | grep 'Logfile directory:' | sed 's/Logfile directory: \([^ ]*\).*/\1/;q'`
echo "Logfile directory: ${logfile_dir}" >> $log

mkdir -p /home/scadmin/clientlogs/${client_id}/${logfile_dir}

number=0
while [ -f "/home/scadmin/clientlogs/${client_id}/${logfile_dir}/${number}.log" ]
do
	number=`expr ${number} + 1`
done

TARGET="/home/scadmin/clientlogs/${client_id}/${logfile_dir}/${number}.log"
echo "Target Log file: ${TARGET}" >> $log
echo "Running on $2" > ${TARGET}
cat /home/scadmin/clientlogs/$2.log >> ${TARGET}
rm /home/scadmin/clientlogs/$2.log

echo "" >> $log
