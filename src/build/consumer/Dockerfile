FROM ubuntu:focal AS builder
ARG app=consumer
USER root
RUN apt-get update && apt-get install -y openjdk-17-jdk
COPY $app /$app
WORKDIR $app
RUN sh gradlew clean bootJar

FROM alpine:latest
ARG app=consumer
RUN apk --no-cache update && \
    apk --no-cache upgrade && \
    apk --no-cache add openjdk17-jre-headless tzdata curl
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk
ENV TZ=Asia/Seoul
EXPOSE 8082
COPY --from=builder /$app/build/libs/*.jar .
COPY build/$app/application.yml .
COPY build/$app/output.cert .
RUN keytool -importcert -cacerts -storepass changeit -file output.cert -alias letsencrypt -noprompt
HEALTHCHECK --start-period=5s \
		CMD curl localhost:8082/actuator/health || exit 1
CMD [ "java", "-Dfile.encoding=utf-8","-jar", "-XX:InitialHeapSize=1M", "-XX:MaxHeapSize=2G", "-XX:MinHeapSize=1M", "/consumer-0.0.1-SNAPSHOT.jar"]
