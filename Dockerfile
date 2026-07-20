# Build image
FROM maven:3.9-eclipse-temurin-21 AS projectbuild
LABEL authors="https://fergalmechin.fr/"

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Final Image
FROM eclipse-temurin:21-jre

# FInal Image Metadata
LABEL org.opencontainers.image.title="basicbooksapi"
LABEL org.opencontainers.image.authors="Fergal MECHIN <pro@fergalmechin.fr>"
LABEL org.opencontainers.image.description="API REST made for self-learning purposes"
LABEL org.opencontainers.image.source="https://github.com/Gretsok/basicbooksapi"

WORKDIR /app

COPY --from=projectbuild /app/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]