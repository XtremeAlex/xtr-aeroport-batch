# The deployment Image
FROM <IMG>

ENV TZ="Europe/Rome"
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /opt/deployment/

# Copy the native executable into the containers
COPY target/*-app xtreme-application

#RUN  chmod +x (Se vuoi semplicemntete dare i permessi di esecuzione)
RUN chmod 775 xtreme-application

EXPOSE 8080

ENTRYPOINT ["./xtreme-application"]
