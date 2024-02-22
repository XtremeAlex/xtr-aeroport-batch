# The deployment Image
FROM busybox:musl

WORKDIR /opt/deployment/

# Copy the native executable into the containers
COPY target/*-app xtreme-application

#RUN  chmod +x (Se vuoi semplicemntete dare i permessi di esecuzione)
RUN chmod 775 xtreme-application

EXPOSE 8080

ENTRYPOINT ["./xtreme-application"]