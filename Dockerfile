FROM maven:3.9.4-eclipse-temurin-17 AS builder

WORKDIR /app

COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

RUN apt-get update && apt-get install -y postgresql-client

EXPOSE 8080

ENV POSTGRES_USER=postgres \
    POSTGRES_PASSWORD=123123 \
    POSTGRES_DB=postgres
ENTRYPOINT ["java", "-jar", "app.jar"]
