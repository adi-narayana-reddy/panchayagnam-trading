# Multi-stage Dockerfile for Flutter Web Frontend
# Stage 1: Build Flutter Web release
FROM ghcr.io/cirruslabs/flutter:stable AS builder
WORKDIR /app

# Cache Flutter pub packages
COPY pubspec.yaml pubspec.lock ./
RUN flutter pub get

COPY . .
RUN flutter build web --release

# Stage 2: Serve using Nginx Alpine
FROM nginx:1.25-alpine
COPY --from=builder /app/build/web /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
