# The deployment Image
FROM docker.io/oraclelinux:8-slim
ARG JAR_FILE=../target/*-jar

ENV TZ="Europe/Rome"
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /opt/deployment/

# Copy the executable into the containers
COPY ${JAR_FILE} xtreme-application.jar

#RUN  chmod +x (Se vuoi semplicemntete dare i permessi di esecuzione)
#RUN chmod 775 xtreme-application.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/xtreme-application.jar"]

EXPOSE 8080
LABEL project="AEROPORT"
