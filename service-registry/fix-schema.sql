-- Script to fix the database schema by dropping and recreating with correct types
-- First, let's drop the foreign key constraints that are causing issues
SET FOREIGN_KEY_CHECKS = 0;

-- Modify the tables to have the correct column types
ALTER TABLE categories MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE environment_types MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE frameworks MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE languages MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE server_types MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE service_types MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE services MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;

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

-- Now recreate the tables that might have been created incorrectly
-- (This will preserve data if the tables already exist with correct structure)

SET FOREIGN_KEY_CHECKS = 1;