# ====== BUILD ======
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build
COPY backend/pom.xml backend/pom.xml
COPY backend/src backend/src
RUN mvn -q -f backend/pom.xml -DskipTests package

# ====== RUN ======
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/backend/target/*.jar app.jar

# Railway te asigna un puerto por la env var PORT
ENV PORT=8080
EXPOSE 8080

# Modo por defecto: web
ENV APP_ROLE=web
# Si APP_ROLE=worker arrancaremos la clase/runner del worker (sección C)
CMD [ "sh", "-c", "if [ \"$APP_ROLE\" = \"worker\" ]; then \
    java -jar app.jar --spring.profiles.active=worker; \
  else \
    java -jar app.jar; \
  fi" ]
