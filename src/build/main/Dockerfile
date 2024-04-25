FROM ubuntu:focal AS builder
ARG APP=main
USER root
RUN apt-get update && apt-get install -y openjdk-17-jdk
COPY $APP /$APP
WORKDIR $APP
RUN sh gradlew clean bootJar

FROM alpine:latest
ARG APP=main
RUN apk --no-cache update && \
    apk --no-cache upgrade && \
    apk --no-cache add openjdk17-jre-headless tzdata
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk
ENV TZ=Asia/Seoul
EXPOSE 8080
COPY build/$APP/application.yml .
COPY --from=builder /$APP/build/libs/*.jar .
COPY build/$APP/output.cert .
RUN keytool -importcert -cacerts -storepass changeit -file output.cert -alias letsencrypt -noprompt
CMD [ "java", "-Dfile.encoding=utf-8","-jar", "-XX:InitialHeapSize=1M", "-XX:MaxHeapSize=2G", "-XX:MinHeapSize=1M", "/main-0.0.1-SNAPSHOT.jar"]
