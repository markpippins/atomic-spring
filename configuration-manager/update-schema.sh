#!/bin/bash

echo "Starting application with temporary configuration to update schema..."

# Run the application with the temporary configuration
SPRING_CONFIG_LOCATION=classpath:/temp-application.properties ./mvnw spring-boot:run