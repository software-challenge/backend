#!/bin/bash
JAR="./software-challenge-server.jar"
echo "Starting game server ($JAR)..."
exec java \
    -Dfile.encoding=UTF-8 \
    -Dlogback.configurationFile=logback-production.xml \
    -Djava.security.egd=file:/dev/./urandom \
    -server \
    -XX:MaxGCPauseMillis=100 \
    -XX:GCPauseIntervalMillis=2050 \
    -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled \
    -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 \
    -XX:+ScavengeBeforeFullGC -XX:+CMSScavengeBeforeRemark \
    -jar $JAR
    #-DLOG_DIRECTORY=/home/scadmin/rails-deployment/current/log \
    # -XX:+PrintGCDateStamps -verbose:gc -XX:+PrintGCDetails -Xloggc:"/home/scadmin/rails-deployment/current/log/game_server_gc_`date +%Y-%m-%d-%H-%M`.log" \
