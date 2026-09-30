FROM nginx:1.25.4
ENV TZ=Asia/Seoul
USER root
RUN apt update && apt install -y git vim net-tools lsof procps
COPY nginx.conf /etc/nginx/
COPY conf/* /etc/nginx/conf.d/
COPY cert /etc/nginx/ssl/
