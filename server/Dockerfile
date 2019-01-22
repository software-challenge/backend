FROM openjdk:8-jre

# Where to get the game server files from. Needs to be below or in directory where this Dockerfile resides. Is the standard build result when executing ant for the game server project.
ARG game_server_dir

ADD $game_server_dir /app
WORKDIR /app
ADD game_server_start_production.sh /app/start.sh
ADD configuration/logback-production.xml /app/logback-production.xml
# This will be overwritten by the docker secret with the real password in production:
ADD configuration/server.properties.production /run/secrets/gameserver_properties
RUN rm /app/server.properties && ln -s /run/secrets/gameserver_properties /app/server.properties
RUN chmod +x /app/start.sh
CMD ["./start.sh"]
