FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/image-upload-autoscaling-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70", "-jar", "app.jar"]
