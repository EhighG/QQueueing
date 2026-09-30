FROM node:20 AS builder
workdir /frontend
copy . .
run npm i
# NEXT_PUBLIC_* 값은 next build 때 번들에 박히므로 빌드 인자로 받는다(demo/compose.yml의 build.args).
ARG NEXT_PUBLIC_BASE_URL=https://localhost/api
ENV NEXT_PUBLIC_BASE_URL=${NEXT_PUBLIC_BASE_URL}
run npm run build
FROM node:20
ENV TZ=Asia/Seoul
EXPOSE 3000
copy --from=builder /frontend /frontend/
#COPY --from=builder /frontend/.next /frontend/.next
#COPY --from=builder /frontend/package.json next.config.mjs /frontend
#COPY --from=builder /frontend/node_modules /frontend/node_modules

WORKDIR /frontend
CMD ["npm", "start"]
