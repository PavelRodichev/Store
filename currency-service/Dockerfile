FROM ghcr.io/graalvm/graalvm-ce:java17-21.3.0
WORKDIR /app
COPY currency-service/target/currency-service-0.0.1-SNAPSHOT.jar appCurrency.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","appCurrency.jar"]