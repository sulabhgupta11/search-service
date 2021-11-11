FROM 911075010171.dkr.ecr.us-east-1.amazonaws.com/openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
RUN mkdir /apps
COPY ${JAR_FILE}  /apps/search-service.jar
RUN mkdir /apps/jks
ARG JKS_FILE=src/main/resources/jks/*.jks
COPY ${JKS_FILE}  /apps/jks/kafka.client.truststore.jks
ENTRYPOINT ["java","-jar","-Dapp.name=search-service", "-Daws.accessKeyId=AKIA5IICNMZ5XFEVM76Z", "-Daws.secretAccessKey=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", "apps/search-service.jar"]
