FROM ubuntu:focal AS builder
USER root
RUN apt-get update && apt-get install -y openjdk-21-jdk
COPY tes24 /tes24
WORKdir tes24
RUN sh gradlew clean bootJar

FROM alpine:latest
RUN apk --no-cache update && \
    apk --no-cache upgrade && \
    apk --no-cache add openjdk21-jre-headless tzdata
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
ENV TZ=Asia/Seoul
EXPOSE 8080
COPY --from=builder /tes24/build/libs/*.jar .
COPY application.yml .
COPY output.cert .
RUN keytool -importcert -cacerts -storepass changeit -file output.cert -alias letsencrypt -noprompt
CMD [ "java", "-Dfile.encoding=utf-8","-jar", "-XX:InitialHeapSize=1M", "-XX:MaxHeapSize=2G", "-XX:MinHeapSize=1M", "/tes24-0.0.1-SNAPSHOT.jar"]
