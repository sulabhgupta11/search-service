FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
RUN mkdir /apps
COPY ${JAR_FILE}  /apps/search-service.jar
RUN mkdir /apps/jks
ARG JKS_FILE=src/main/resources/jks/*.jks
COPY ${JKS_FILE}  /apps/jks/kafka.client.truststore.jks
ENTRYPOINT ["java","-jar","-Dapp.name=search-service", "apps/search-service.jar"]
