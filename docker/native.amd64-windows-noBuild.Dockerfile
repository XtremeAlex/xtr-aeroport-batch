#IN FASE DI TEST
# The deployment Image
#FROM microsoft/aspnet:4.6.2-windowsservercore
#FROM mcr.microsoft.com/dotnet/aspnet:8.0
FROM mcr.microsoft.com/windows:20H2-amd64

WORKDIR /deployment

# Copy the native executable into the container
COPY ../target/*.exe xtreme-application.exe
#Copy the lib into the container
COPY ../target/*.dll .

#RUN  chmod +x (Se vuoi semplicemntete dare i permessi di esecuzione)
#RUN chmod 775 xtreme-application.exe

EXPOSE 8080

ENTRYPOINT ["xtreme-application.exe"]
