-- Database schema fix script
-- Run this script to fix column type mismatches before starting the application

-- First, let's check if the tables exist and what the current column types are
SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'services_console'
AND TABLE_NAME IN ('categories', 'environment_types', 'frameworks', 'languages', 'server_types', 'services', 'service_types')
AND COLUMN_NAME = 'id';

-- Modify columns to ensure they are all BIGINT with AUTO_INCREMENT
-- Note: May need to drop and recreate foreign key constraints if they exist

-- Modify primary key columns to BIGINT with AUTO_INCREMENT
ALTER TABLE categories MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE environment_types MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE frameworks MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE languages MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE server_types MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE services MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE service_types MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;

-- Modify foreign key columns to BIGINT
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

-- If you get foreign key constraint errors, you may need to drop them first:
/*
SET FOREIGN_KEY_CHECKS = 0;

-- Then run the ALTER TABLE statements

SET FOREIGN_KEY_CHECKS = 1;
*/