FROM ghcr.io/graalvm/graalvm-ce:java17-21.3.0
WORKDIR /app
COPY target/currency-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]