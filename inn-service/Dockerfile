FROM ghcr.io/graalvm/graalvm-ce:java17-21.3.0
WORKDIR /app
COPY target/inn-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","appInn.jar"]