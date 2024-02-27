FROM ghcr.io/graalvm/native-image:ol8-java17-22 AS builder

# Install tar and gzip to extract the Maven binaries
RUN microdnf update \
 && microdnf install --nodocs \
    tar \
    gzip \
 && microdnf clean all \
 && rm -rf /var/cache/yum

# Install Maven
ARG USER_HOME_DIR="/root"
ARG SHA=706f01b20dec0305a822ab614d51f32b07ee11d0218175e55450242e49d2156386483b506b3a4e8a03ac8611bae96395fd5eec15f50d3013d5deed6d1ee18224
ARG MAVEN_DOWNLOAD_URL=https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz

RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
  && curl -fsSL -o /tmp/apache-maven.tar.gz ${MAVEN_DOWNLOAD_URL} \
  && echo "${SHA}  /tmp/apache-maven.tar.gz" | sha512sum -c - \
  && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
  && rm -f /tmp/apache-maven.tar.gz \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

WORKDIR /opt/build

# Copy the source code into the image for building
COPY src src
COPY pom.xml pom.xml

# Avvia la build nativa
RUN mvn package -Pnative -DskipTests
    #native:compile

# The deployment Image
FROM docker.io/oraclelinux:8-slim

ENV TZ="Europe/Rome"
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /opt/deployment/

# Copy the native executable into the containers
COPY --from=builder /opt/build/target/*-app xtreme-application

#RUN  chmod +x (Se vuoi semplicemntete dare i permessi di esecuzione)
RUN chmod 775 xtreme-application

EXPOSE 8080

ENTRYPOINT ["./xtreme-application"]
