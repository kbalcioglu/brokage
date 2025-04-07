# Use the Amazon Corretto JDK which has better ARM64 support for Apple Silicon
FROM amazoncorretto:21.0.3

WORKDIR /app
COPY build/libs/*.jar app.jar
EXPOSE 8080

# Critical flags for M4 Mac compatibility
ENTRYPOINT ["java", "-XX:-UseSIMDForArrayEquals", "-XX:+UseSerialGC", "-jar", "app.jar"]