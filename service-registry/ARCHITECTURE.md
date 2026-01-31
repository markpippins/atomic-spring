# Host Server Architecture

## Overview

The Host Server is a comprehensive service management system designed to handle the cognitive load of managing multiple microservices across different frameworks and deployment environments in the Atomic Platform.

## Design Principles

### 1. **Framework Agnostic**
Support for any framework that can be integrated into the platform:
- Java: Spring Boot, Quarkus, Micronaut
- Node.js: NestJS, AdonisJS, Moleculer, Express
- Python: Django, Flask, FastAPI
- Others: .NET, Go, Rust, etc.

### 2. **Environment Aware**
Track services across multiple environments:
- Development
- Staging
- Production
- Test

### 3. **Dependency Tracking**
Maintain service dependency graphs for:
- Impact analysis
- Deployment ordering
- Troubleshooting
- Architecture visualization

### 4. **Configuration Management**
Centralized configuration with:
- Environment-specific overrides
- Secret management
- Type safety
- Audit trails

### 5. **Deployment Tracking**
Monitor service instances with:
- Health status
- Version tracking
- Resource allocation
- Lifecycle management

## Data Model

### Entity Relationship Diagram

```
┌─────────────┐
│  Framework  │
└──────┬──────┘
       │ 1:N
       │
┌──────▼──────┐         ┌─────────────────┐
│   Service   │◄────────┤ ServiceConfig   │
└──────┬──────┘   1:N   └─────────────────┘
       │ N:M (dependencies)
       │ ┌──────────┐
       └─┤ Service  │
         └──────────┘
       │ 1:N
       │
┌──────▼──────┐
│ Deployment  │
└──────┬──────┘
       │ N:1
       │
┌──────▼──────┐
│   Server    │
└─────────────┘
```

### Entity Details

#### Framework
- **Purpose**: Catalog of technology frameworks
- **Key Attributes**: name, category, language, version, broker support
- **Relationships**: Has many Services

#### Service
- **Purpose**: Microservice or application definition
- **Key Attributes**: name, type, port, health check, API path
- **Relationships**: 
  - Belongs to Framework
  - Has many Deployments
  - Has many Configurations
  - Has many Dependencies (self-referential)

#### Server (Host)
- **Purpose**: Physical or virtual server
- **Key Attributes**: hostname, IP, type, environment, resources
- **Relationships**: Has many Deployments

#### Deployment
- **Purpose**: Service instance on a server
- **Key Attributes**: port, version, status, health, environment
- **Relationships**: 
  - Belongs to Service
  - Belongs to Server

#### ServiceConfiguration
- **Purpose**: Environment-specific configuration
- **Key Attributes**: key, value, environment, type, secret flag
- **Relationships**: Belongs to Service

## API Design

### RESTful Principles

All endpoints follow REST conventions:
- `GET` - Retrieve resources
- `POST` - Create resources
- `PUT` - Update resources
- `DELETE` - Remove resources

### Resource Hierarchy

```
/api/frameworks
/api/services
  ├── /{id}/dependencies
  └── /{id}/dependents
/api/servers
/api/deployments
  ├── /{id}/start
  ├── /{id}/stop
  └── /{id}/health
/api/configurations
```

### Query Patterns

1. **By ID**: `/api/services/{id}`
2. **By Name**: `/api/services/name/{name}`
3. **By Category**: `/api/frameworks/category/{category}`
4. **By Relationship**: `/api/deployments/service/{serviceId}`
5. **By Status**: `/api/servers/status/{status}`

## Use Cases

### 1. Service Discovery
**Scenario**: Find all services using a specific framework

```
GET /api/frameworks/name/Spring Boot
GET /api/services/framework/{frameworkId}
```

### 2. Impact Analysis
**Scenario**: Determine which services will be affected if user-service goes down

```
GET /api/services/name/user-service
GET /api/services/{id}/dependents
```

### 3. Deployment Planning
**Scenario**: Plan deployment order based on dependencies

```
GET /api/services
GET /api/services/{id}/dependencies (for each service)
```

### 4. Environment Configuration
**Scenario**: Get all production configurations for a service

```
GET /api/configurations/service/{id}/environment/PRODUCTION
```

### 5. Health Monitoring
**Scenario**: Check health of all running deployments

```
GET /api/deployments/status/RUNNING
```

### 6. Capacity Planning
**Scenario**: View server resources and deployment distribution

```
GET /api/servers
GET /api/deployments/server/{serverId}
```

### 7. Framework Migration
**Scenario**: Identify services to migrate from one framework to another

```
GET /api/frameworks/name/Spring Boot
GET /api/services/framework/{oldFrameworkId}
```

## Integration Points

### 1. Broker Gateway Integration
Services with `supportsBrokerPattern=true` can:
- Register dynamically with broker-gateway
- Participate in service orchestration
- Use centralized routing

### 2. CI/CD Integration
Deployment API can be used by CI/CD pipelines to:
- Create new deployments
- Update deployment status
- Track deployment history

### 3. Monitoring Integration
Health check endpoints can feed into:
- Prometheus/Grafana
- ELK Stack
- Custom monitoring dashboards

### 4. Configuration Management
Configuration API can integrate with:
- Spring Cloud Config
- Consul
- Vault for secrets

## Security Considerations

### Current State (Development)
- No authentication/authorization
- CORS enabled for all origins
- H2 in-memory database

### Production Recommendations

1. **Authentication**: Add Spring Security with JWT/OAuth2
2. **Authorization**: Role-based access control (RBAC)
3. **CORS**: Restrict to known origins
4. **Database**: Migrate to PostgreSQL/MySQL with encryption
5. **Secrets**: Integrate with HashiCorp Vault or AWS Secrets Manager
6. **Audit Logging**: Track all configuration changes
7. **API Rate Limiting**: Prevent abuse
8. **HTTPS**: Enforce TLS for all connections

## Scalability

### Current Architecture
- Single instance
- In-memory database
- Synchronous REST API

### Scaling Strategies

1. **Horizontal Scaling**: Multiple instances behind load balancer
2. **Database**: Persistent database with connection pooling
3. **Caching**: Redis for frequently accessed data
4. **Async Processing**: Message queue for long-running operations
5. **Read Replicas**: Separate read/write databases
6. **API Gateway**: Rate limiting and request routing

## Future Enhancements

### Phase 1: Monitoring
- Automated health checks
- Metrics collection
- Alerting system

### Phase 2: Automation
- Deployment automation
- Configuration sync
- Service registration

### Phase 3: Advanced Features
- Service mesh integration
- Container orchestration (Kubernetes)
- Multi-region support
- Disaster recovery

### Phase 4: Intelligence
- Dependency visualization
- Performance analytics
- Capacity forecasting
- Anomaly detection

## Technology Stack

- **Framework**: Spring Boot 3.3.10
- **Language**: Java 21
- **Database**: H2 (development), PostgreSQL/MySQL (production)
- **ORM**: Spring Data JPA
- **Build**: Maven
- **API**: REST with JSON

## Development Workflow

### Adding New Entity

1. Create entity class in `entity/` package
2. Create repository interface in `repository/` package
3. Create controller in `controller/` package
4. Update `DataInitializer` for sample data
5. Add tests
6. Update documentation

### Adding New Endpoint

1. Add method to appropriate controller
2. Add repository query if needed
3. Test with curl/Postman
4. Document in API_EXAMPLES.md
5. Update README.md

## Testing Strategy

### Unit Tests
- Entity validation
- Repository queries
- Business logic

### Integration Tests
- Controller endpoints
- Database operations
- Transaction management

### End-to-End Tests
- Complete workflows
- Dependency scenarios
- Configuration management

## Deployment

### Development
```bash
mvn spring-boot:run
```

### Production
```bash
mvn clean package
java -jar target/host-server-1.0.0-SNAPSHOT.jar
```

### Docker
```dockerfile
FROM openjdk:21-jdk-slim
COPY target/host-server-1.0.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Monitoring

### Actuator Endpoints
- `/actuator/health` - Health status
- `/actuator/info` - Application info
- `/actuator/metrics` - Metrics

### Custom Metrics
- Service count by framework
- Deployment count by environment
- Configuration count by type
- Server resource utilization

## Conclusion

The Host Server provides a solid foundation for managing the growing complexity of the Atomic Platform's microservices ecosystem. Its flexible data model and comprehensive API enable effective service management, dependency tracking, and configuration management across multiple frameworks and environments.
