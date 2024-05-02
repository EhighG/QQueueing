FROM ubuntu:focal AS builder
ARG APP=main
USER root
RUN apt-get update && apt-get install -y openjdk-21-jdk
COPY $APP /$APP
WORKDIR $APP
RUN sh gradlew clean bootJar

#FROM ubuntu:focal
#USER root
#WORKDIR /
#RUN apt update && apt install -y openjdk-21-jre curl
#RUN apt install -y apt-transport-https ca-certificates gnupg-agent software-properties-common
#RUN curl -fsSL https://download.docker.com/linux/ubuntu/gpg | apt-key add -
#RUN add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu focal stable"
#RUN apt update
#RUN apt install -y docker-ce docker-ce-cli containerd.io docker-compose docker-compose-plugin
#ARG APP=main
#ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
#ENV TZ=Asia/Seoul
#EXPOSE 8081
#COPY build/$APP/application.yml .
#COPY --from=builder /$APP/build/libs/*.jar .
#COPY build/$APP/output.cert .
#RUN keytool -importcert -cacerts -storepass changeit -file output.cert -alias letsencrypt -noprompt
#CMD [ "java", "-Dfile.encoding=utf-8","-jar", "-XX:InitialHeapSize=1M", "-XX:MaxHeapSize=2G", "-XX:MinHeapSize=1M", "/main-0.0.1-SNAPSHOT.jar"]

FROM alpine:latest
ARG APP=main
RUN apk --no-cache update && \
    apk --no-cache upgrade && \
    apk --no-cache add openjdk21-jre-headless tzdata
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
ENV TZ=Asia/Seoul
EXPOSE 8081
COPY build/$APP/application.yml .
COPY --from=builder /$APP/build/libs/*.jar .
COPY build/$APP/output.cert .
RUN keytool -importcert -cacerts -storepass changeit -file output.cert -alias letsencrypt -noprompt
CMD [ "java", "-Dfile.encoding=utf-8","-jar", "-XX:InitialHeapSize=1M", "-XX:MaxHeapSize=2G", "-XX:MinHeapSize=1M", "/main-0.0.1-SNAPSHOT.jar"]
