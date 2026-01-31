# Host Server - Service Management System

A comprehensive server/service/configuration management system for the Atomic Platform. This service provides centralized management of servers, services, frameworks, deployments, and configurations across the entire microservices ecosystem.

## Overview

The Host Server is a production-ready service management system providing centralized management of servers, services, frameworks, deployments, and configurations across the entire microservices ecosystem. It serves as the central service registry for the Atomic Platform and handles the growing complexity of managing multiple services across different frameworks (Spring Boot, Quarkus, Micronaut, NestJS, AdonisJS, Moleculer, etc.) using the broker pattern.

### ✅ **PRODUCTION READY CAPABILITIES**

- **Service Registration**: External services register via `/api/registry/register`
- **Service Discovery**: Operation-based service lookup with `/api/registry/services/by-operation/{operation}`
- **Service Details**: Complete service information with `/api/registry/services/{serviceName}/details`
- **Heartbeat Monitoring**: Continuous health monitoring with `/api/registry/heartbeat/{serviceName}`
- **MySQL Persistence**: Production-grade persistent storage (migrated from H2)
- **Polyglot Support**: Framework-agnostic service integration
- **Real-time Updates**: Live service status tracking
- **Deployment Management**: Complete service instance tracking across servers

## New Feature: Service Backend Connections

Track relationships between service instances (deployments). Essential for modeling:
- file-service → file-system-server connections
- Primary/backup configurations
- Multi-backend setups

**Quick Start**: See [BACKEND_QUICK_START.md](BACKEND_QUICK_START.md)  
**Full API Docs**: See [BACKEND_CONNECTIONS_API.md](BACKEND_CONNECTIONS_API.md)

## Architecture

### Data Model

```
Framework (Spring, Quarkus, NestJS, etc.)
    ↓
Service (broker-gateway, user-service, etc.)
    ↓
Deployment (service instance on a server)
    ↓
Server (physical/virtual host)

Service ← ServiceConfiguration (environment-specific configs)
Service ← Service (dependencies)
```

### Core Entities

#### 1. **Framework**
Represents technology frameworks used across the platform.

**Attributes:**
- Name, description, category
- Language (Java, TypeScript, Python, etc.)
- Latest version, documentation URL
- Broker pattern support flag

**Categories:**
- `JAVA_SPRING`, `JAVA_QUARKUS`, `JAVA_MICRONAUT`
- `NODE_EXPRESS`, `NODE_NESTJS`, `NODE_ADONISJS`, `NODE_MOLECULER`
- `PYTHON_DJANGO`, `PYTHON_FLASK`, `PYTHON_FASTAPI`
- `DOTNET_ASPNET`, `GO_GOA`, `RUST_ACTIX`, `OTHER`

#### 2. **Service**
Represents a microservice or application.

**Attributes:**
- Name, description, version
- Framework reference
- Service type (REST_API, GATEWAY, DATABASE, etc.)
- Repository URL, default port
- Health check path, API base path
- Status (ACTIVE, DEPRECATED, ARCHIVED, PLANNED)

**Relationships:**
- Belongs to a Framework
- Has many Deployments
- Has many Configurations
- Has many Dependencies (other Services)

#### 3. **Server (Host)**
Represents physical or virtual servers.

**Attributes:**
- Hostname, IP address
- Server type (PHYSICAL, VIRTUAL, CONTAINER, CLOUD)
- Environment (DEVELOPMENT, STAGING, PRODUCTION, TEST)
- OS, CPU cores, memory, disk
- Region, cloud provider
- Status (ACTIVE, INACTIVE, MAINTENANCE, DECOMMISSIONED)

#### 4. **Deployment**
Represents a service instance running on a server.

**Attributes:**
- Service and Server references
- Port, context path, version
- Status (RUNNING, STOPPED, STARTING, STOPPING, FAILED)
- Environment (DEVELOPMENT, STAGING, PRODUCTION, TEST)
- Health check URL and status
- Process ID, container name
- Deployment timestamps

#### 5. **ServiceConfiguration**
Environment-specific configuration for services.

**Attributes:**
- Service reference
- Config key/value pairs
- Environment (ALL, DEVELOPMENT, STAGING, PRODUCTION, TEST)
- Type (STRING, NUMBER, BOOLEAN, JSON, URL, DATABASE_URL, API_KEY)
- Secret flag
- Description

## REST API

### Frameworks

```
GET    /api/frameworks                    - List all frameworks
GET    /api/frameworks/{id}               - Get framework by ID
GET    /api/frameworks/name/{name}        - Get framework by name
GET    /api/frameworks/category/{cat}     - List by category
GET    /api/frameworks/language/{lang}    - List by language
GET    /api/frameworks/broker-compatible  - List broker-compatible frameworks
POST   /api/frameworks                    - Create framework
PUT    /api/frameworks/{id}               - Update framework
DELETE /api/frameworks/{id}               - Delete framework
```

### Services

```
GET    /api/services                           - List all services
GET    /api/services/{id}                      - Get service by ID
GET    /api/services/name/{name}               - Get service by name
GET    /api/services/framework/{frameworkId}   - List by framework
GET    /api/services/type/{type}               - List by type
GET    /api/services/status/{status}           - List by status
GET    /api/services/{id}/dependencies         - Get service dependencies
GET    /api/services/{id}/dependents           - Get dependent services
POST   /api/services                           - Create service
PUT    /api/services/{id}                      - Update service
POST   /api/services/{id}/dependencies/{depId} - Add dependency
DELETE /api/services/{id}/dependencies/{depId} - Remove dependency
DELETE /api/services/{id}                      - Delete service
```

### Servers

```
GET    /api/servers                        - List all servers
GET    /api/servers/{id}                   - Get server by ID
GET    /api/servers/hostname/{hostname}    - Get server by hostname
GET    /api/servers/environment/{env}      - List by environment
GET    /api/servers/status/{status}        - List by status
GET    /api/servers/type/{type}            - List by type
POST   /api/servers                        - Create server
PUT    /api/servers/{id}                   - Update server
DELETE /api/servers/{id}                   - Delete server
```

### Deployments

```
GET    /api/deployments                                    - List all deployments
GET    /api/deployments/{id}                               - Get deployment by ID
GET    /api/deployments/service/{serviceId}                - List by service
GET    /api/deployments/server/{serverId}                  - List by server
GET    /api/deployments/status/{status}                    - List by status
GET    /api/deployments/environment/{env}                  - List by environment
GET    /api/deployments/service/{id}/environment/{env}     - List by service & env
POST   /api/deployments                                    - Create deployment
PUT    /api/deployments/{id}                               - Update deployment
POST   /api/deployments/{id}/start                         - Start deployment
POST   /api/deployments/{id}/stop                          - Stop deployment
POST   /api/deployments/{id}/health?healthStatus={status}  - Update health status
DELETE /api/deployments/{id}                               - Delete deployment
```

### Configurations

```
GET    /api/configurations                                      - List all configs
GET    /api/configurations/{id}                                 - Get config by ID
GET    /api/configurations/service/{serviceId}                  - List by service
GET    /api/configurations/service/{id}/environment/{env}       - List by service & env
GET    /api/configurations/service/{id}/key/{key}/environment/{env} - Get specific config
POST   /api/configurations                                      - Create config
PUT    /api/configurations/{id}                                 - Update config
DELETE /api/configurations/{id}                                 - Delete config
```

## Running the Service

### Standalone

```bash
cd spring/host-server
./mvnw spring-boot:run
```

The service will start on port **8085**.

### Access Database

The application now uses MySQL database instead of H2.

- JDBC URL: `jdbc:mysql://localhost:3306/services_console`
- Username: `root`
- Password: `rootpass`

## Sample Data

The service initializes with sample data including:

### Frameworks
- Spring Boot 3.5.0 (Java, broker-compatible)
- Quarkus 3.15.1 (Java, broker-compatible)
- Micronaut 4.0.0 (Java)
- NestJS 10.0.0 (TypeScript)
- AdonisJS 6.0.0 (TypeScript)
- Moleculer 0.14.0 (TypeScript, broker-compatible)

### Services
- broker-gateway (Spring Boot, port 8080)
- user-service (Spring Boot, port 8083)
- login-service (Spring Boot, port 8082)
- file-service (Spring Boot, port 4040)
- note-service (Spring Boot, port 8084)
- quarkus-broker-gateway (Quarkus, port 8190)
- moleculer-search (Moleculer, port 4050)

### Servers
- localhost (127.0.0.1, Development environment)

### Deployments
- broker-gateway on localhost:8080
- user-service on localhost:8083

## Use Cases

### 1. Service Discovery
Query all services by framework, type, or status to understand the service landscape.

### 2. Dependency Mapping
Track service dependencies to understand impact of changes and deployment order.

### 3. Environment Management
Manage configurations across different environments (dev, staging, production).

### 4. Deployment Tracking
Monitor which services are deployed where, their status, and health.

### 5. Framework Adoption
Track which frameworks are in use and plan migrations or new service development.

### 6. Capacity Planning
View server resources and deployment distribution for capacity planning.

## Integration with Broker Pattern

Services marked with `supportsBrokerPattern=true` can integrate with the broker-gateway for:
- Dynamic service registration
- Request routing
- Service orchestration
- Health monitoring

## Future Enhancements

1. **Health Monitoring**: Automated health checks for deployments
2. **Metrics Collection**: Performance metrics and resource usage
3. **Deployment Automation**: API endpoints to trigger deployments
4. **Configuration Sync**: Push configurations to running services
5. **Service Mesh Integration**: Integration with Istio/Linkerd
6. **Audit Logging**: Track all changes to services and configurations
7. **API Gateway Integration**: Sync with broker-gateway service registry
8. **Container Orchestration**: Kubernetes/Docker integration
9. **Alerting**: Notifications for service failures or health issues
10. **Version Management**: Track service versions and rollback capabilities

## Technology Stack

- **Framework**: Spring Boot 3.5.0
- **Language**: Java 21
- **Database**: H2 (in-memory, for development)
- **ORM**: Spring Data JPA
- **Build Tool**: Maven

## Migration Path

For production use, migrate from H2 to a persistent database:

1. Add MySQL/PostgreSQL dependency to `pom.xml`
2. Update `application.properties` with database connection
3. Change `spring.jpa.hibernate.ddl-auto` to `update` or `validate`
4. Consider adding Flyway/Liquibase for schema migrations

## Database Schema Issues

If you encounter database schema compatibility errors during startup (such as "wrong column type encountered" errors), you have two options:

### Option 1: Run the automated database fix script

```bash
# Make the script executable
chmod +x run-db-fix.sh

# Run the database fix script
./run-db-fix.sh
```

### Option 2: Use the temporary configuration to update the schema

If the automated script doesn't work, you can temporarily update the schema using Hibernate:

```bash
# Make the script executable
chmod +x update-schema.sh

# Run the schema update script
./update-schema.sh
```

This will start the application with `ddl-auto=update` to fix the schema, then you can stop it with Ctrl+C.

### Option 3: Run the SQL commands manually

```bash
# Connect to your MySQL database and run:
mysql -u root -p services_console < database-fix.sql
```

After applying any of these fixes, restart the application normally. This resolves column type incompatibilities between the database schema and entity definitions.

## Recommended Solution

The recommended approach is to fix the database schema to ensure column types match the entity definitions:

1. If you have access to MySQL directly, run the database fix script:
   ```bash
   ./fix-database.sh
   ```

2. Then start the application normally:
   ```bash
   # From the project root
   cd /path/to/project
   mvn spring-boot:run -pl host-server
   ```

3. The application.properties is configured to use `ddl-auto=validate` mode by default, which ensures the schema matches the entity definitions.

If you don't have direct MySQL access, the application will attempt to fix schema issues on startup using the DatabaseSchemaValidator.
