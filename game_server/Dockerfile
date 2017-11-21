FROM openjdk:8-jre

# Where to get the game server files from. Needs to be below or in directory where this Dockerfile resides. Is the standard build result when executing ant for the game server project.
ARG game_server_dir

ADD $game_server_dir /app
WORKDIR /app
ADD game_server_start_production.sh /app/start.sh
ADD configuration/logback-release.xml /app/logback-production.xml
RUN chmod +x /app/start.sh
CMD ./start.sh
