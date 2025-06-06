# ---- Build Stage ----
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /build

# Copy and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application without running tests
RUN mvn clean package -DskipTests

# ---- Runtime Stage ----
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /build/target/chitchathub-1.0.0.jar app.jar

# Optionally set active profile (e.g., prod/dev)
#ARG PROFILE=prod
#ENV SPRING_PROFILES_ACTIVE=${PROFILE}

# Expose port
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
