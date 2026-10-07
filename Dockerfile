# --- Build stage ---
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew installDist --no-daemon

# --- Run stage ---
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/build/install/kotlin-ktor-demo-app/ .
EXPOSE 8080
ENTRYPOINT ["bin/kotlin-ktor-demo-app"]
