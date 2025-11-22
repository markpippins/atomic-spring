#!/bin/bash

# Host Server Startup Script

echo "Starting Host Server..."
echo "Port: 8085"
echo "H2 Console: http://localhost:8085/h2-console"
echo ""

cd "$(dirname "$0")"

# Check if Maven is available
if command -v mvn &> /dev/null; then
    mvn spring-boot:run
else
    echo "Maven not found. Please install Maven or use the Maven wrapper."
    exit 1
fi
