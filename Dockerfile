# syntax=docker/dockerfile:1.7
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN sed -i 's/\r$//' mvnw
RUN chmod +x mvnw

COPY src/ src/
RUN --mount=type=cache,target=/root/.m2 ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8180

ENTRYPOINT ["java", "-jar", "app.jar"]
