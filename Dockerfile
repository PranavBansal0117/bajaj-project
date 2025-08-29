FROM openjdk:11-jdk-slim

COPY *.java /app/
WORKDIR /app

RUN javac *.java

EXPOSE $PORT

CMD ["java", "BfhlApiApplication"]
