#!/bin/bash

echo "Starting service-registry to fix database schema..."

# Start the application to update the schema
echo "Starting application with ddl-auto=update to fix schema..."
./mvnw spring-boot:run

echo "Application stopped. Schema should now be updated."
echo "For production use, consider changing ddl-auto back to 'validate' mode."