FROM ghcr.io/graalvm/graalvm-ce:java17-21.3.0
WORKDIR /app
COPY target/store-0.0.1-SNAPSHOT.jar appStore.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","appStore.jar"]