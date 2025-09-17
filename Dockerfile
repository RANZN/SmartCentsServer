# -------------------------------
# Stage 1: Build the fat JAR
# -------------------------------
FROM eclipse-temurin:17-jdk-focal AS builder

WORKDIR /app

# Copy Gradle wrapper & settings
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy the source code
COPY src ./src

# Make wrapper executable & build shadow JAR
RUN chmod +x ./gradlew && ./gradlew shadowJar --no-daemon

# -------------------------------
# Stage 2: Minimal runtime image
# -------------------------------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy only the shadow JAR, rename to app.jar
COPY --from=builder /app/build/libs/*-all.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]