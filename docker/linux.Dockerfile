FROM ghcr.io/graalvm/native-image:ol8-java17-22 AS builder

WORKDIR /opt/build

# Copy the source code into the image for building
COPY mvnw mvnw
COPY .mvn .mvn
COPY src src
COPY pom.xml pom.xml

# Rendi eseguibile mvnw e avvia la build nativa
RUN chmod +x mvnw && ./mvnw package -DskipTests

# The deployment Image
FROM docker.io/oraclelinux:8-slim

ENV TZ="Europe/Rome"
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /opt/deployment/

# Copy the native executable into the containers
COPY --from=builder /opt/build/target/*-jar xtreme-application.jar

#RUN  chmod +x (Se vuoi semplicemntete dare i permessi di esecuzione)
RUN chmod 775 xtreme-application.jar

EXPOSE 8080

ENTRYPOINT ["java -jar xtreme-application.jar"]
