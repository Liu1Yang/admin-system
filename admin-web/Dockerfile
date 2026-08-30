# Day46：多阶段构建 — Node 打包 + Nginx 托管 dist
FROM node:20-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
# 生产构建：VITE_API_BASE_URL 留空，浏览器同源访问，由 nginx 反代 /api
RUN npm run build

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
