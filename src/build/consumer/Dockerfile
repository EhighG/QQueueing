FROM ubuntu:focal
USER root
WORKDIR /
RUN apt update && apt install -y openjdk-17-jre
ENV TZ=Asia/Seoul
EXPOSE 8080
COPY tes24-0.0.1-SNAPSHOT.jar .
COPY application.yml .
COPY output.cert .
RUN keytool -importcert -cacerts -storepass changeit -file output.cert -alias letsencrypt -noprompt
CMD ["java", "-Dfile.encoding=utf-8","-jar", "-XX:InitialHeapSize=1M", "-XX:MaxHeapSize=2G", "-XX:MinHeapSize=1M", "/tes24-0.0.1-SNAPSHOT.jar"]
