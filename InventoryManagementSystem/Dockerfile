FROM openjdk:22-jdk-slim

WORKDIR /inventorysystem

MAINTAINER ravindra
WORKDIR /inventorysystem
COPY target/InventoryManagementSystem-0.0.1-SNAPSHOT.jar /inventorysystem/InventoryManagementSystem-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/InventoryManagementSystem-0.0.1-SNAPSHOT.jar"]
EXPOSE 8086
