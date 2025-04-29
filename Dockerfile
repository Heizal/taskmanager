# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk

# Set the working directory in the container
WORKDIR /app

# Copy Maven wrapper and project files
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy the project source
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Expose the port Spring Boot uses
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/taskmanager-0.0.1-SNAPSHOT.jar"]
