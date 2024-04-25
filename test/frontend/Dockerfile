from bitnami/node:20
ENV TZ=Asia/Seoul
EXPOSE 3000
WORKDIR /
COPY .next /frontend/.next
COPY package.json next.config.mjs /frontend
COPY node_modules /frontend/node_modules
WORKDIR /frontend

CMD ["npm", "start"]
