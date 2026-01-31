# Host Server Quick Reference

## Quick Start

```bash
# Start the service
cd spring/host-server
mvn spring-boot:run

# Or use scripts
./start.sh      # Linux/Mac
start.bat       # Windows
```

**Access:**
- API: http://localhost:8085/api/
- H2 Console: http://localhost:8085/h2-console

## Common Commands

### List Everything
```bash
curl http://localhost:8085/api/frameworks
curl http://localhost:8085/api/services
curl http://localhost:8085/api/servers
curl http://localhost:8085/api/deployments
curl http://localhost:8085/api/configurations
```

### Get Service Info
```bash
# By name
curl http://localhost:8085/api/services/name/broker-gateway

# With dependencies
curl http://localhost:8085/api/services/1/dependencies

# With dependents
curl http://localhost:8085/api/services/1/dependents
```

### Get Deployments
```bash
# All deployments for a service
curl http://localhost:8085/api/deployments/service/1

# All deployments on a server
curl http://localhost:8085/api/deployments/server/1

# Running deployments
curl http://localhost:8085/api/deployments/status/RUNNING
```

### Get Configurations
```bash
# All configs for a service
curl http://localhost:8085/api/configurations/service/1

# Development configs
curl http://localhost:8085/api/configurations/service/1/environment/DEVELOPMENT

# Specific config
curl "http://localhost:8085/api/configurations/service/1/key/server.port/environment/ALL"
```

## Entity Quick Reference

### Framework
```json
{
  "name": "Spring Boot",
  "category": "JAVA_SPRING",
  "language": "Java",
  "latestVersion": "3.5.0",
  "supportsBrokerPattern": true
}
```

**Categories:** `JAVA_SPRING`, `JAVA_QUARKUS`, `JAVA_MICRONAUT`, `NODE_NESTJS`, `NODE_ADONISJS`, `NODE_MOLECULER`, `PYTHON_DJANGO`, `PYTHON_FLASK`, `PYTHON_FASTAPI`, `DOTNET_ASPNET`, `GO_GOA`, `RUST_ACTIX`, `OTHER`

### Service
```json
{
  "name": "broker-gateway",
  "framework": {"id": 1},
  "type": "GATEWAY",
  "defaultPort": 8080,
  "healthCheckPath": "/actuator/health",
  "apiBasePath": "/api/broker",
  "status": "ACTIVE"
}
```

**Types:** `REST_API`, `GRAPHQL_API`, `GRPC_SERVICE`, `MESSAGE_QUEUE`, `DATABASE`, `CACHE`, `GATEWAY`, `PROXY`, `WEB_APP`, `BACKGROUND_JOB`

**Status:** `ACTIVE`, `DEPRECATED`, `ARCHIVED`, `PLANNED`

### Server
```json
{
  "hostname": "localhost",
  "ipAddress": "127.0.0.1",
  "type": "VIRTUAL",
  "environment": "DEVELOPMENT",
  "operatingSystem": "Windows 11",
  "cpuCores": 8,
  "memoryMb": 16384,
  "diskGb": 512,
  "status": "ACTIVE"
}
```

**Types:** `PHYSICAL`, `VIRTUAL`, `CONTAINER`, `CLOUD`

**Environments:** `DEVELOPMENT`, `STAGING`, `PRODUCTION`, `TEST`

**Status:** `ACTIVE`, `INACTIVE`, `MAINTENANCE`, `DECOMMISSIONED`

### Deployment
```json
{
  "service": {"id": 1},
  "server": {"id": 1},
  "port": 8080,
  "version": "1.0.0",
  "status": "RUNNING",
  "environment": "DEVELOPMENT",
  "healthCheckUrl": "http://localhost:8080/actuator/health",
  "healthStatus": "HEALTHY"
}
```

**Status:** `RUNNING`, `STOPPED`, `STARTING`, `STOPPING`, `FAILED`, `UNKNOWN`

**Health:** `HEALTHY`, `UNHEALTHY`, `DEGRADED`, `UNKNOWN`

### Configuration
```json
{
  "service": {"id": 1},
  "configKey": "server.port",
  "configValue": "8080",
  "environment": "ALL",
  "type": "NUMBER",
  "isSecret": false
}
```

**Environments:** `ALL`, `DEVELOPMENT`, `STAGING`, `PRODUCTION`, `TEST`

**Types:** `STRING`, `NUMBER`, `BOOLEAN`, `JSON`, `URL`, `DATABASE_URL`, `API_KEY`

## PowerShell Quick Commands

```powershell
# Get all services
$services = Invoke-RestMethod -Uri "http://localhost:8085/api/services"
$services | Format-Table name, type, defaultPort

# Get service by name
$service = Invoke-RestMethod -Uri "http://localhost:8085/api/services/name/broker-gateway"
$service | ConvertTo-Json

# Create service
$body = @{
    name = "new-service"
    framework = @{ id = 1 }
    type = "REST_API"
    defaultPort = 8090
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8085/api/services" `
    -Method Post -Body $body -ContentType "application/json"

# Get dependencies
$deps = Invoke-RestMethod -Uri "http://localhost:8085/api/services/1/dependencies"
$deps | ForEach-Object { Write-Host $_.name }
```

## Sample Data IDs

### Frameworks
- 1: Spring Boot
- 2: Quarkus
- 3: Micronaut
- 4: NestJS
- 5: AdonisJS
- 6: Moleculer

### Services
- 1: broker-gateway
- 2: user-service
- 3: login-service
- 4: file-service
- 5: note-service
- 6: quarkus-broker-gateway
- 7: moleculer-search

### Servers
- 1: localhost

## Common Workflows

### Add New Service
```bash
# 1. Create service
curl -X POST http://localhost:8085/api/services \
  -H "Content-Type: application/json" \
  -d '{"name":"my-service","framework":{"id":1},"type":"REST_API","defaultPort":8090}'

# 2. Add config
curl -X POST http://localhost:8085/api/configurations \
  -H "Content-Type: application/json" \
  -d '{"service":{"id":8},"configKey":"server.port","configValue":"8090","environment":"ALL","type":"NUMBER"}'

# 3. Create deployment
curl -X POST http://localhost:8085/api/deployments \
  -H "Content-Type: application/json" \
  -d '{"service":{"id":8},"server":{"id":1},"port":8090,"environment":"DEVELOPMENT","status":"STOPPED"}'
```

### Check Service Health
```bash
# Get all deployments for service
curl http://localhost:8085/api/deployments/service/1

# Check health status
curl http://localhost:8085/api/deployments/1 | jq '.healthStatus'
```

### Find Dependencies
```bash
# What does this service depend on?
curl http://localhost:8085/api/services/3/dependencies

# What depends on this service?
curl http://localhost:8085/api/services/2/dependents
```

### Environment Configs
```bash
# Get all dev configs
curl http://localhost:8085/api/configurations/service/1/environment/DEVELOPMENT

# Get all prod configs
curl http://localhost:8085/api/configurations/service/1/environment/PRODUCTION
```

## Troubleshooting

### Service won't start
```bash
# Check if port is already in use
netstat -ano | findstr :8085

# Check Database Connection
# The application now uses MySQL instead of H2
# JDBC URL: jdbc:mysql://localhost:3306/services_console
# Username: root
# Password: rootpass
```

### Can't connect to API
```bash
# Verify service is running
curl http://localhost:8085/actuator/health

# Check logs
# Look in console output for errors
```

### Data not showing
```bash
# Check if DataInitializer ran
# Look for "Initializing sample data..." in logs

# Verify database
# Connect to H2 console and run:
# SELECT * FROM frameworks;
# SELECT * FROM services;
```

## Tips

1. **Use jq for JSON parsing:**
   ```bash
   curl http://localhost:8085/api/services | jq '.[] | {name, type, port: .defaultPort}'
   ```

2. **Save responses to files:**
   ```bash
   curl http://localhost:8085/api/services > services.json
   ```

3. **Pretty print in PowerShell:**
   ```powershell
   Invoke-RestMethod -Uri "http://localhost:8085/api/services" | ConvertTo-Json -Depth 10
   ```

4. **Test with Postman:**
   - Import the API endpoints
   - Create environment with base URL
   - Save common requests

5. **Monitor logs:**
   ```bash
   # Increase logging level in application.properties
   logging.level.com.angrysurfer.atomic.hostserver=DEBUG
   ```

## Next Steps

1. Explore the full API documentation: `README.md`
2. Try the examples: `API_EXAMPLES.md`
3. Understand the architecture: `ARCHITECTURE.md`
4. View diagrams: `DIAGRAMS.md`
5. Read implementation details: `IMPLEMENTATION_SUMMARY.md`
