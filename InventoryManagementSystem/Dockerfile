FROM maven:3.9.4-eclipse-temurin-17 as build

WORKDIR /workspace

# copy only what we need for a reproducible build
COPY pom.xml mvnw ./
COPY .mvn .mvn
COPY src src

RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre

WORKDIR /app

# copy built jar (artifact name follows the module POM)
COPY --from=build /workspace/target/inventory-management-system-Dev_v2.6.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java","-jar","/app.jar"]