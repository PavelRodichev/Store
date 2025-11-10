FROM ghcr.io/graalvm/graalvm-ce:java17-21.3.0

WORKDIR /app

# Копируем JAR файл
COPY target/email-service-0.0.1-SNAPSHOT.jar app.jar

# Указываем порт
EXPOSE 8085

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]