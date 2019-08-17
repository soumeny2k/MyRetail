FROM openjdk:8-jdk-alpine

COPY build/libs/myRetail.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]