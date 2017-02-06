FROM openjdk:8-jre

ADD jars/producer.jar /app/producer.jar
WORKDIR /app
VOLUME /app/watch-folder
VOLUME /app/temp-folder

CMD java -jar producer.jar -w /app/watch-folder -t /app/temp-folder -h rabbitmq