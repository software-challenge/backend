FROM openjdk:11-jre

# Where to get the game server files from. Needs to be below or in directory
# where this Dockerfile resides. Is the standard build result when executing
# ant for the game server project.
ARG game_server_dir

ADD $game_server_dir /app
WORKDIR /app
# the following files from the configuration directory will be copied to the
# docker working directory before build
ADD logback.xml /app/logback.xml
# This will be overwritten by the docker secret with the real password in production:
ADD server.properties.production /run/secrets/gameserver_properties
RUN rm /app/server.properties && \
      ln -s /run/secrets/gameserver_properties /app/server.properties

ENTRYPOINT ["java"]

CMD ["-Dfile.encoding=UTF-8", \
     "-Djava.security.egd=file:/dev/./urandom", \
     "-server", \
     "-Xmx16g", \
     "-XX:MaxGCPauseMillis=100", \
     "-XX:GCPauseIntervalMillis=2050", \
     "-verbose:gc", \
     "-Xlog:gc*=trace:file=\"/app/gc.log\":tags,time,uptime,level", \
     "-jar", \
     "./server.jar"]
