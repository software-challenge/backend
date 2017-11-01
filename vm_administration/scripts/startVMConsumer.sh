#!/bin/bash

# ----------------------------------------------------------------------
# startVMConsumer.sh
#
# wrapper fuer das JavaProgramm 'daemonconsumer.jar'
# Der DaemonConsumer ueberwacht die Queue vm-queue auf der VMMain. Fuer
# jeden Queue-Eintrag wird das Skript zum Starten eine neuen VMClient
# ausgefuehrt.
#
# Parameter:
# -h Host auf dem der Queue-Server laeuft
# -q Name der Queue
# -b Programm das ausgefuehrt werden soll
# -c Argumente fuer das Programm
# -m Maximale Anzahl gleichzeitig laufender Prozesse
# -i Interval, in dem die Queue abgefragt wird
#
# (c) 2009-2011 software-challenge
#
# HISTORY
#
# 27.01.2011 wib Historie begonnen
# 28.01.2011 sca Kommentar mit Aufrufparametern ergaenzt
# 04.02.2011 wib Logfile vor dem Neustart sichern
#

# ----------------------------------------------------------------------
# globals
#
export PATH=/bin:/usr/sbin:/home/vbox/bin

VMMAIN=134.245.253.5
DAEMON=$HOME/bin/daemonconsumer.jar
START=$HOME/bin/startVM.sh
LOG=$HOME/log/consumer.log

# ----------------------------------------------------------------------
# main
#
if [ ! -f $DAEMON ]; then
  echo "ERROR: '$DAEMON' does not exists. abort"
  exit 1
fi

if [ ! -x $START ]; then
  echo "ERROR: '$START' does not exists or is not executable. abort"
  exit 1
fi

if [ -f $LOG ]; then
  mv $LOG $LOG.old
fi

#exec java -jar $DAEMON -h $VMMAIN -q vm-queue -b /bin/bash -c $START -m 12 -i 5 > $LOG 2>&1
exec java -jar $DAEMON -h $VMMAIN -q swc-job-queue -b /bin/bash -c /home/vbox/bin/startVM.sh -m 12 -i 5 > $LOG 2>&1

# ----------------------------------------------------------------------
# end of file
# ----------------------------------------------------------------------
