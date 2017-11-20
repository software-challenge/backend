#!/bin/sh
ant
cd deploy/server/
java -Dfile.encoding=UTF-8 \
     -Dlogback.configurationFile=logback.xml \
     -XX:+PrintGCDateStamps -verbose:gc -XX:+PrintGCDetails -Xloggc:"gc.log" \
     -jar softwarechallenge-server.jar &
echo "started server"
sleep 2
tail -f gc.log
