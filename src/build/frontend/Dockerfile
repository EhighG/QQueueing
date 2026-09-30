FROM node:20 AS builder
ARG app=frontend
WORKDIR /$app
COPY $app /$app
RUN npm i
# NEXT_PUBLIC_* 값은 next build 때 번들에 박힌다. 값은 compose가 build.args로 넘긴다(src/compose.yml).
ARG NEXT_PUBLIC_BASE_URL
ARG NEXT_PUBLIC_MONITORING_URL
ENV NEXT_PUBLIC_BASE_URL=$NEXT_PUBLIC_BASE_URL
ENV NEXT_PUBLIC_MONITORING_URL=$NEXT_PUBLIC_MONITORING_URL
RUN npm run build

FROM node:20
ARG app=frontend
ENV TZ=Asia/Seoul
EXPOSE 3000
WORKDIR /$app
COPY --from=builder /$app/.next ./.next
COPY --from=builder /$app/package.json /$app/next.config.mjs ./
COPY --from=builder /$app/node_modules ./node_modules
CMD ["npm", "start"]
