FROM nginx:1.25.3
USER root
ENV TZ=Asia/Seoul
COPY default.conf /etc/nginx/conf.d/
COPY nginx.conf /etc/nginx/
COPY .next /etc/nginx/.next
RUN chmod 664 /etc/nginx/.next
EXPOSE 80
