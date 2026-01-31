#!/bin/bash

echo "Running database schema fix for service-registry..."

# Check if mysql client is available
if ! command -v mysql &> /dev/null; then
    echo "MySQL client is not installed or not in PATH"
    exit 1
fi

# Prompt for MySQL password (or you can set it as an environment variable)
read -s -p "Enter MySQL root password: " MYSQL_ROOT_PASSWORD
echo ""

# Run the database fix script
echo "Applying database schema fixes..."
mysql -u root -p"$MYSQL_ROOT_PASSWORD" services_console < database-fix.sql

if [ $? -eq 0 ]; then
    echo "Database schema fixes applied successfully!"
else
    echo "Error applying database schema fixes."
    exit 1
fi

echo "Database schema fix completed. You can now start the application normally."