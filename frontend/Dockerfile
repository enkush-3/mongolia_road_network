FROM node:20-alpine AS builder
WORKDIR /build

RUN corepack enable && corepack prepare pnpm@9.0.0 --activate   # pnpm 9 нь Node 20-тай нийцнэ

COPY package.json pnpm-lock.yaml ./
RUN pnpm install --frozen-lockfile

COPY . .
RUN pnpm run build

FROM nginx:alpine
COPY --from=builder /build/dist /usr/share/nginx/html
RUN echo 'server { listen 5137; location / { root /usr/share/nginx/html; try_files $uri $uri/ /index.html; } }' > /etc/nginx/conf.d/default.conf
EXPOSE 5137
CMD ["nginx", "-g", "daemon off;"]
