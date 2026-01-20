#!/bin/bash

echo "Dropping and recreating the services_console database..."

# Prompt for MySQL password
read -s -p "Enter MySQL root password: " MYSQL_ROOT_PASSWORD
echo ""

# Connect to MySQL and execute commands
mysql -u root -p"$MYSQL_ROOT_PASSWORD" << EOF
DROP DATABASE IF EXISTS services_console;
CREATE DATABASE services_console;
GRANT ALL PRIVILEGES ON services_console.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
EOF

if [ $? -eq 0 ]; then
    echo "Database services_console has been dropped and recreated successfully!"
    echo "The application will now recreate the schema on next startup."
else
    echo "Error dropping and recreating the database."
    exit 1
fi