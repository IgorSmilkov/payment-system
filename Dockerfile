# Use an official OpenJDK 21 runtime as a parent image
FROM openjdk:21

# Set the working directory
WORKDIR /app

# Copy the packaged jar file into the container
COPY target/payment-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8443

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
