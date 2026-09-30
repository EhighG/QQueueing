FROM bitnami/node:20 as builder
workdir /frontend
copy . .
run npm i
run npm run build
FROM bitnami/node:20
ENV TZ=Asia/Seoul
EXPOSE 3000
copy --from=builder /frontend /frontend/
#COPY --from=builder /frontend/.next /frontend/.next
#COPY --from=builder /frontend/package.json next.config.mjs /frontend
#COPY --from=builder /frontend/node_modules /frontend/node_modules

WORKDIR /frontend
CMD ["npm", "start"]
