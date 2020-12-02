FROM openjdk:8-jre

# Where to get the game server files from. Needs to be below or in directory where this Dockerfile resides. Is the standard build result when executing ant for the game server project.
ARG game_server_dir

ADD $game_server_dir /app
WORKDIR /app
ADD configuration/logback-production.xml /app/logback-production.xml
# This will be overwritten by the docker secret with the real password in production:
ADD configuration/server.properties.production /run/secrets/gameserver_properties
RUN rm /app/server.properties && ln -s /run/secrets/gameserver_properties /app/server.properties

ENTRYPOINT ["java"]
CMD ["-Dfile.encoding=UTF-8", \
     "-Dlogback.configurationFile=logback-production.xml", \
     "-Djava.security.egd=file:/dev/./urandom", \
     "-server", \
     "-XX:MaxGCPauseMillis=100", \
     "-XX:GCPauseIntervalMillis=2050", \
     "-XX:+UseConcMarkSweepGC", \
     "-XX:+CMSParallelRemarkEnabled", \
     "-XX:+UseCMSInitiatingOccupancyOnly", \
     "-XX:CMSInitiatingOccupancyFraction=70", \
     "-XX:+ScavengeBeforeFullGC", \
     "-XX:+CMSScavengeBeforeRemark", \
     "-jar", \
     "./server.jar"]
