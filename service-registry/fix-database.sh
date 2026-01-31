#!/bin/bash

echo "Fixing database schema for host-server..."

# Prompt for MySQL password
read -s -p "Enter MySQL root password: " MYSQL_ROOT_PASSWORD
echo ""

# Connect to MySQL and execute commands to fix the schema
mysql -u root -p"$MYSQL_ROOT_PASSWORD" << EOF
USE services_console;

-- Disable foreign key checks temporarily
SET FOREIGN_KEY_CHECKS = 0;

-- Fix the ID columns to be BIGINT AUTO_INCREMENT
ALTER TABLE categories MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE environment_types MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE frameworks MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE languages MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE server_types MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE service_types MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE services MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;

-- Fix the foreign key columns to be BIGINT
ALTER TABLE frameworks MODIFY COLUMN category_id BIGINT NOT NULL;
ALTER TABLE frameworks MODIFY COLUMN language_id BIGINT NOT NULL;
ALTER TABLE frameworks MODIFY COLUMN vendor_id BIGINT NOT NULL;

ALTER TABLE services MODIFY COLUMN framework_id BIGINT NOT NULL;
ALTER TABLE services MODIFY COLUMN service_type_id BIGINT NOT NULL;

ALTER TABLE deployments MODIFY COLUMN service_id BIGINT NOT NULL;
ALTER TABLE deployments MODIFY COLUMN environment_id BIGINT NOT NULL;
ALTER TABLE deployments MODIFY COLUMN server_id BIGINT NOT NULL;

ALTER TABLE servers MODIFY COLUMN environment_type_id BIGINT NOT NULL;
ALTER TABLE servers MODIFY COLUMN server_type_id BIGINT NOT NULL;
ALTER TABLE servers MODIFY COLUMN operating_system_id BIGINT NOT NULL;

ALTER TABLE service_configs MODIFY COLUMN service_id BIGINT NOT NULL;
ALTER TABLE service_configs MODIFY COLUMN config_type_id BIGINT NOT NULL;
ALTER TABLE service_configs MODIFY COLUMN environment_id BIGINT NOT NULL;

ALTER TABLE service_dependencies MODIFY COLUMN service_id BIGINT NOT NULL;
ALTER TABLE service_dependencies MODIFY COLUMN target_service_id BIGINT NOT NULL;

ALTER TABLE service_libraries MODIFY COLUMN service_id BIGINT NOT NULL;
ALTER TABLE service_libraries MODIFY COLUMN library_id BIGINT NOT NULL;

ALTER TABLE libraries MODIFY COLUMN language_id BIGINT;
ALTER TABLE libraries MODIFY COLUMN category_id BIGINT;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Show the corrected column types
SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'services_console' 
AND TABLE_NAME IN ('categories', 'environment_types', 'frameworks', 'languages', 'server_types', 'services', 'service_types')
AND COLUMN_NAME = 'id';

SELECT 'Schema fix completed!' as Status;
EOF

if [ $? -eq 0 ]; then
    echo "Database schema has been fixed successfully!"
    echo "You can now start the application with 'validate' mode."
else
    echo "Error fixing the database schema."
    exit 1
fi