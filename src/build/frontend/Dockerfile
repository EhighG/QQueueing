FROM bitnami/node:20 as builder
ARG app=frontend
WORKDIR /$app
COPY $app /$app
RUN npm i
RUN npm run build

FROM bitnami/node:20
ARG app=frontend
ENV TZ=Asia/Seoul
EXPOSE 3000
WORKDIR /$app
COPY --from=builder /$app/.next ./.next
COPY --from=builder /$app/package.json /$app/next.config.mjs ./
COPY --from=builder /$app/node_modules ./node_modules
#CMD ["npm", "start"]
COPY --from=builder /$app .

CMD ["npm", "run", "dev"]
