FROM openjdk:8-jdk-alpine

COPY build/libs/casestudy.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]