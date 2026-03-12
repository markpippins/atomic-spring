#!/bin/bash

echo "Fixing database schema for service-registry..."

# First, let's temporarily change ddl-auto to update to fix the schema
echo "Temporarily changing ddl-auto to update..."
sed -i 's/spring.jpa.hibernate.ddl-auto=validate/spring.jpa.hibernate.ddl-auto=update/g' src/main/resources/application.properties

echo "Starting application to update schema..."
./mvnw spring-boot:run &

# Wait for the application to start and update the schema
sleep 30

# Kill the application
pkill -f "spring-boot:run"

echo "Schema update completed."

# Change ddl-auto back to validate
echo "Restoring ddl-auto to validate..."
sed -i 's/spring.jpa.hibernate.ddl-auto=update/spring.jpa.hibernate.ddl-auto=validate/g' src/main/resources/application.properties

echo "Database schema fix completed. You can now start the application normally."