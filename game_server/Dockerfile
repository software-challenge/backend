FROM openjdk:8-jre

ADD game-server-build /app
WORKDIR /app
RUN chmod +x /app/start.sh
CMD ./start.sh