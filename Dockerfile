FROM openjdk:11-jre-slim 
COPY *.java /app/ 
RUN javac *.java 
EXPOSE $PORT 
CMD ["java", "BfhlApiApplication"] 
