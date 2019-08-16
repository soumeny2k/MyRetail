FROM openjdk:8-jdk-alpine

COPY build/libs/Myretail.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]