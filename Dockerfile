FROM eclipse-temurin:17-jdk-alpine
LABEL authors="katai"
VOLUME /app
COPY target/*.jar ecommerce.jar
ENTRYPOINT ["java", "-jar", "/ecommerce.jar"]
