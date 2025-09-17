# Stage 1: Build the application using a JDK image
FROM eclipse-temurin:17-jdk-focal AS builder

# Set the working directory to the root of the app
WORKDIR /app

# Copy the Gradle wrapper files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Copy the application source code
COPY src ./src

# Make the Gradle wrapper executable and build the "fat JAR"
# This command compiles the code and packages it with all dependencies.
RUN chmod +x ./gradlew && ./gradlew build shadowJar

# Stage 2: Create a minimal final image for running the application
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Copy only the final "fat JAR" from the builder stage
COPY --from=builder /app/build/libs/shadow.jar .

EXPOSE 8080

# The command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "shadow.jar"]

