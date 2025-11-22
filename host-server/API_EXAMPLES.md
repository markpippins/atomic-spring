# Host Server API Examples

This document provides practical examples for using the Host Server REST API.

## Base URL

```
http://localhost:8085
```

## Frameworks API

### List All Frameworks

```bash
curl http://localhost:8085/api/frameworks
```

### Get Spring Boot Framework

```bash
curl http://localhost:8085/api/frameworks/name/Spring%20Boot
```

### List Java Frameworks

```bash
curl http://localhost:8085/api/frameworks/category/JAVA_SPRING
```

### List Broker-Compatible Frameworks

```bash
curl http://localhost:8085/api/frameworks/broker-compatible
```

### Create New Framework (Micronaut)

```bash
curl -X POST http://localhost:8085/api/frameworks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Micronaut",
    "description": "Modern JVM-based framework",
    "category": "JAVA_MICRONAUT",
    "language": "Java",
    "latestVersion": "4.0.0",
    "documentationUrl": "https://micronaut.io",
    "supportsBrokerPattern": false
  }'
```

## Services API

### List All Services

```bash
curl http://localhost:8085/api/services
```

### Get Service by Name

```bash
curl http://localhost:8085/api/services/name/broker-gateway
```

### List Services by Framework

```bash
# Get Spring Boot framework ID first, then:
curl http://localhost:8085/api/services/framework/1
```

### List Gateway Services

```bash
curl http://localhost:8085/api/services/type/GATEWAY
```

### Get Service Dependencies

```bash
curl http://localhost:8085/api/services/3/dependencies
```

### Get Services That Depend On This Service

```bash
curl http://localhost:8085/api/services/2/dependents
```

### Create New Service

```bash
curl -X POST http://localhost:8085/api/services \
  -H "Content-Type: application/json" \
  -d '{
    "name": "payment-service",
    "description": "Payment processing service",
    "framework": {"id": 1},
    "type": "REST_API",
    "defaultPort": 8090,
    "healthCheckPath": "/actuator/health",
    "apiBasePath": "/api/payments",
    "status": "ACTIVE",
    "version": "1.0.0"
  }'
```

### Add Service Dependency

```bash
# Make service 5 depend on service 2
curl -X POST http://localhost:8085/api/services/5/dependencies/2
```

### Remove Service Dependency

```bash
curl -X DELETE http://localhost:8085/api/services/5/dependencies/2
```

## Servers API

### List All Servers

```bash
curl http://localhost:8085/api/servers
```

### Get Server by Hostname

```bash
curl http://localhost:8085/api/servers/hostname/localhost
```

### List Production Servers

```bash
curl http://localhost:8085/api/servers/environment/PRODUCTION
```

### List Active Servers

```bash
curl http://localhost:8085/api/servers/status/ACTIVE
```

### Create New Server

```bash
curl -X POST http://localhost:8085/api/servers \
  -H "Content-Type: application/json" \
  -d '{
    "hostname": "prod-server-01",
    "ipAddress": "192.168.1.100",
    "type": "VIRTUAL",
    "environment": "PRODUCTION",
    "operatingSystem": "Ubuntu 22.04",
    "cpuCores": 16,
    "memoryMb": 32768,
    "diskGb": 1024,
    "region": "us-east-1",
    "cloudProvider": "AWS",
    "status": "ACTIVE",
    "description": "Production application server"
  }'
```

### Update Server Status

```bash
curl -X PUT http://localhost:8085/api/servers/1 \
  -H "Content-Type: application/json" \
  -d '{
    "hostname": "localhost",
    "ipAddress": "127.0.0.1",
    "type": "VIRTUAL",
    "environment": "DEVELOPMENT",
    "operatingSystem": "Windows 11",
    "cpuCores": 8,
    "memoryMb": 16384,
    "diskGb": 512,
    "status": "MAINTENANCE"
  }'
```

## Deployments API

### List All Deployments

```bash
curl http://localhost:8085/api/deployments
```

### List Deployments for a Service

```bash
curl http://localhost:8085/api/deployments/service/1
```

### List Deployments on a Server

```bash
curl http://localhost:8085/api/deployments/server/1
```

### List Running Deployments

```bash
curl http://localhost:8085/api/deployments/status/RUNNING
```

### List Production Deployments

```bash
curl http://localhost:8085/api/deployments/environment/PRODUCTION
```

### Create New Deployment

```bash
curl -X POST http://localhost:8085/api/deployments \
  -H "Content-Type: application/json" \
  -d '{
    "service": {"id": 1},
    "server": {"id": 1},
    "port": 8080,
    "version": "1.0.0",
    "status": "STOPPED",
    "environment": "DEVELOPMENT",
    "healthCheckUrl": "http://localhost:8080/actuator/health",
    "deploymentPath": "/opt/services/broker-gateway"
  }'
```

### Start Deployment

```bash
curl -X POST http://localhost:8085/api/deployments/1/start
```

### Stop Deployment

```bash
curl -X POST http://localhost:8085/api/deployments/1/stop
```

### Update Health Status

```bash
curl -X POST "http://localhost:8085/api/deployments/1/health?healthStatus=HEALTHY"
```

## Configurations API

### List All Configurations

```bash
curl http://localhost:8085/api/configurations
```

### List Configurations for a Service

```bash
curl http://localhost:8085/api/configurations/service/1
```

### List Development Configurations for a Service

```bash
curl http://localhost:8085/api/configurations/service/1/environment/DEVELOPMENT
```

### Get Specific Configuration

```bash
curl "http://localhost:8085/api/configurations/service/1/key/server.port/environment/ALL"
```

### Create Configuration

```bash
curl -X POST http://localhost:8085/api/configurations \
  -H "Content-Type: application/json" \
  -d '{
    "service": {"id": 1},
    "configKey": "spring.data.mongodb.uri",
    "configValue": "mongodb://localhost:27017/mydb",
    "environment": "DEVELOPMENT",
    "type": "DATABASE_URL",
    "isSecret": false,
    "description": "MongoDB connection string"
  }'
```

### Create Secret Configuration

```bash
curl -X POST http://localhost:8085/api/configurations \
  -H "Content-Type: application/json" \
  -d '{
    "service": {"id": 1},
    "configKey": "api.secret.key",
    "configValue": "super-secret-key-12345",
    "environment": "PRODUCTION",
    "type": "API_KEY",
    "isSecret": true,
    "description": "API secret key for external service"
  }'
```

### Update Configuration

```bash
curl -X PUT http://localhost:8085/api/configurations/1 \
  -H "Content-Type: application/json" \
  -d '{
    "service": {"id": 1},
    "configKey": "server.port",
    "configValue": "8081",
    "environment": "ALL",
    "type": "NUMBER",
    "isSecret": false,
    "description": "Updated server port"
  }'
```

## Complex Queries

### Get Complete Service Information

```bash
# Get service details
SERVICE_ID=1
curl http://localhost:8085/api/services/$SERVICE_ID

# Get its deployments
curl http://localhost:8085/api/deployments/service/$SERVICE_ID

# Get its configurations
curl http://localhost:8085/api/configurations/service/$SERVICE_ID

# Get its dependencies
curl http://localhost:8085/api/services/$SERVICE_ID/dependencies

# Get services that depend on it
curl http://localhost:8085/api/services/$SERVICE_ID/dependents
```

### Get Server Deployment Overview

```bash
# Get server details
SERVER_ID=1
curl http://localhost:8085/api/servers/$SERVER_ID

# Get all deployments on this server
curl http://localhost:8085/api/deployments/server/$SERVER_ID
```

### Environment Overview

```bash
# List all production servers
curl http://localhost:8085/api/servers/environment/PRODUCTION

# List all production deployments
curl http://localhost:8085/api/deployments/environment/PRODUCTION

# Get production configs for a service
curl http://localhost:8085/api/configurations/service/1/environment/PRODUCTION
```

## PowerShell Examples

### Get All Services (PowerShell)

```powershell
$response = Invoke-RestMethod -Uri "http://localhost:8085/api/services" -Method Get
$response | ConvertTo-Json -Depth 10
```

### Create Service (PowerShell)

```powershell
$body = @{
    name = "notification-service"
    description = "Email and SMS notifications"
    framework = @{ id = 1 }
    type = "REST_API"
    defaultPort = 8095
    healthCheckPath = "/actuator/health"
    apiBasePath = "/api/notifications"
    status = "ACTIVE"
    version = "1.0.0"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8085/api/services" `
    -Method Post `
    -Body $body `
    -ContentType "application/json"
```

### Get Service Dependencies (PowerShell)

```powershell
$serviceId = 3
$deps = Invoke-RestMethod -Uri "http://localhost:8085/api/services/$serviceId/dependencies" -Method Get
$deps | ForEach-Object { Write-Host "$($_.name) - $($_.description)" }
```

## Integration Scenarios

### Scenario 1: Onboard New Service

```bash
# 1. Create the service
SERVICE_RESPONSE=$(curl -X POST http://localhost:8085/api/services \
  -H "Content-Type: application/json" \
  -d '{
    "name": "analytics-service",
    "description": "Analytics and reporting",
    "framework": {"id": 1},
    "type": "REST_API",
    "defaultPort": 8100,
    "healthCheckPath": "/actuator/health",
    "apiBasePath": "/api/analytics",
    "status": "ACTIVE",
    "version": "1.0.0"
  }')

SERVICE_ID=$(echo $SERVICE_RESPONSE | jq -r '.id')

# 2. Add configurations
curl -X POST http://localhost:8085/api/configurations \
  -H "Content-Type: application/json" \
  -d "{
    \"service\": {\"id\": $SERVICE_ID},
    \"configKey\": \"server.port\",
    \"configValue\": \"8100\",
    \"environment\": \"ALL\",
    \"type\": \"NUMBER\",
    \"isSecret\": false
  }"

# 3. Create deployment
curl -X POST http://localhost:8085/api/deployments \
  -H "Content-Type: application/json" \
  -d "{
    \"service\": {\"id\": $SERVICE_ID},
    \"server\": {\"id\": 1},
    \"port\": 8100,
    \"version\": \"1.0.0\",
    \"status\": \"STOPPED\",
    \"environment\": \"DEVELOPMENT\"
  }"
```

### Scenario 2: Deploy Service to Production

```bash
# 1. Get or create production server
PROD_SERVER_ID=2

# 2. Create production deployment
curl -X POST http://localhost:8085/api/deployments \
  -H "Content-Type: application/json" \
  -d '{
    "service": {"id": 1},
    "server": {"id": 2},
    "port": 8080,
    "version": "1.2.0",
    "status": "STOPPED",
    "environment": "PRODUCTION",
    "healthCheckUrl": "http://prod-server:8080/actuator/health"
  }'

# 3. Start the deployment
DEPLOYMENT_ID=5
curl -X POST http://localhost:8085/api/deployments/$DEPLOYMENT_ID/start

# 4. Update health status
curl -X POST "http://localhost:8085/api/deployments/$DEPLOYMENT_ID/health?healthStatus=HEALTHY"
```

### Scenario 3: Service Dependency Analysis

```bash
# Find all services that depend on user-service
USER_SERVICE_ID=2
curl http://localhost:8085/api/services/$USER_SERVICE_ID/dependents

# Get the full dependency tree for broker-gateway
BROKER_ID=1
curl http://localhost:8085/api/services/$BROKER_ID/dependencies
```
