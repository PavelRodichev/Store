FROM ghcr.io/graalvm/graalvm-ce:java17-21.3.0
WORKDIR /app
COPY inn-service/target/inn-service-0.0.1-SNAPSHOT.jar appInn.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","appInn.jar"]