# Host Server Visual Diagrams

## System Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         Host Server                              │
│                      (Port 8085)                                 │
│                                                                   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │  Framework   │  │   Service    │  │    Server    │          │
│  │  Management  │  │  Management  │  │  Management  │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│                                                                   │
│  ┌──────────────┐  ┌──────────────┐                            │
│  │  Deployment  │  │Configuration │                            │
│  │  Management  │  │  Management  │                            │
│  └──────────────┘  └──────────────┘                            │
│                                                                   │
│                    ┌──────────────┐                             │
│                    │  H2 Database │                             │
│                    └──────────────┘                             │
└─────────────────────────────────────────────────────────────────┘
```

## Entity Relationship Diagram

```
┌─────────────────────┐
│     Framework       │
│─────────────────────│
│ id (PK)             │
│ name                │
│ description         │
│ category            │
│ language            │
│ latestVersion       │
│ documentationUrl    │
│ supportsBrokerPattern│
└──────────┬──────────┘
           │ 1
           │
           │ N
┌──────────▼──────────┐         ┌─────────────────────┐
│      Service        │         │ ServiceConfiguration│
│─────────────────────│         │─────────────────────│
│ id (PK)             │◄────────│ id (PK)             │
│ name                │    1:N  │ service_id (FK)     │
│ description         │         │ configKey           │
│ framework_id (FK)   │         │ configValue         │
│ type                │         │ environment         │
│ repositoryUrl       │         │ type                │
│ version             │         │ isSecret            │
│ defaultPort         │         │ description         │
│ healthCheckPath     │         └─────────────────────┘
│ apiBasePath         │
│ status              │
└──────────┬──────────┘
           │ N:M (self-referential)
           │ ┌──────────────────┐
           └─┤ service_dependencies│
             └──────────────────┘
           │ 1
           │
           │ N
┌──────────▼──────────┐
│    Deployment       │
│─────────────────────│
│ id (PK)             │
│ service_id (FK)     │
│ server_id (FK)      │
│ port                │
│ contextPath         │
│ version             │
│ status              │
│ environment         │
│ healthCheckUrl      │
│ healthStatus        │
│ processId           │
│ containerName       │
│ deploymentPath      │
│ deployedAt          │
│ startedAt           │
│ stoppedAt           │
└──────────┬──────────┘
           │ N
           │
           │ 1
┌──────────▼──────────┐
│      Server         │
│─────────────────────│
│ id (PK)             │
│ hostname            │
│ ipAddress           │
│ type                │
│ environment         │
│ operatingSystem     │
│ cpuCores            │
│ memoryMb            │
│ diskGb              │
│ region              │
│ cloudProvider       │
│ status              │
│ description         │
└─────────────────────┘
```

## Service Dependency Graph (Sample Data)

```
                    ┌──────────────────┐
                    │  broker-gateway  │
                    │  (Spring Boot)   │
                    │  Port: 8080      │
                    └────────┬─────────┘
                             │
                ┌────────────┼────────────┐
                │            │            │
                ▼            ▼            ▼
        ┌───────────┐  ┌──────────┐  ┌──────────┐
        │user-service│  │login-svc │  │file-svc  │
        │(Spring)    │  │(Spring)  │  │(Spring)  │
        │Port: 8083  │  │Port: 8082│  │Port: 4040│
        └─────┬──────┘  └────┬─────┘  └──────────┘
              │              │
              └──────────────┘
                     │
                     ▼
              ┌──────────┐
              │note-svc  │
              │(Spring)  │
              │Port: 8084│
              └──────────┘
```

## Deployment Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    localhost (127.0.0.1)                     │
│                    Development Environment                    │
│─────────────────────────────────────────────────────────────│
│                                                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │ broker-gateway  │  │  user-service   │  │ login-svc   │ │
│  │   Port: 8080    │  │   Port: 8083    │  │ Port: 8082  │ │
│  │   Status: ✓     │  │   Status: ✓     │  │ Status: ✓   │ │
│  │   Health: ✓     │  │   Health: ✓     │  │ Health: ✓   │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
│                                                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │  file-service   │  │  note-service   │  │ host-server │ │
│  │   Port: 4040    │  │   Port: 8084    │  │ Port: 8085  │ │
│  │   Status: ✓     │  │   Status: ✓     │  │ Status: ✓   │ │
│  │   Health: ✓     │  │   Health: ✓     │  │ Health: ✓   │ │
│  └─────────────────┘  └─────────────────┘  └─────────────┘ │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

## Configuration Hierarchy

```
Service: broker-gateway
│
├── ALL Environments
│   ├── server.port = 8080
│   ├── spring.application.name = broker-gateway
│   └── logging.level.root = INFO
│
├── DEVELOPMENT
│   ├── spring.data.mongodb.uri = mongodb://localhost:27017/broker
│   ├── logging.level.com.angrysurfer = DEBUG
│   └── spring.devtools.restart.enabled = true
│
├── STAGING
│   ├── spring.data.mongodb.uri = mongodb://staging-db:27017/broker
│   └── logging.level.com.angrysurfer = INFO
│
└── PRODUCTION
    ├── spring.data.mongodb.uri = mongodb://prod-db:27017/broker
    ├── api.secret.key = ********** (secret)
    └── logging.level.com.angrysurfer = WARN
```

## API Endpoint Structure

```
Host Server API (http://localhost:8085/api)
│
├── /frameworks
│   ├── GET    /                      (List all)
│   ├── GET    /{id}                  (Get by ID)
│   ├── GET    /name/{name}           (Get by name)
│   ├── GET    /category/{category}   (List by category)
│   ├── GET    /language/{language}   (List by language)
│   ├── GET    /broker-compatible     (List broker-compatible)
│   ├── POST   /                      (Create)
│   ├── PUT    /{id}                  (Update)
│   └── DELETE /{id}                  (Delete)
│
├── /services
│   ├── GET    /                           (List all)
│   ├── GET    /{id}                       (Get by ID)
│   ├── GET    /name/{name}                (Get by name)
│   ├── GET    /framework/{frameworkId}    (List by framework)
│   ├── GET    /type/{type}                (List by type)
│   ├── GET    /status/{status}            (List by status)
│   ├── GET    /{id}/dependencies          (Get dependencies)
│   ├── GET    /{id}/dependents            (Get dependents)
│   ├── POST   /                           (Create)
│   ├── POST   /{id}/dependencies/{depId}  (Add dependency)
│   ├── PUT    /{id}                       (Update)
│   ├── DELETE /{id}/dependencies/{depId}  (Remove dependency)
│   └── DELETE /{id}                       (Delete)
│
├── /servers
│   ├── GET    /                        (List all)
│   ├── GET    /{id}                    (Get by ID)
│   ├── GET    /hostname/{hostname}     (Get by hostname)
│   ├── GET    /environment/{env}       (List by environment)
│   ├── GET    /status/{status}         (List by status)
│   ├── GET    /type/{type}             (List by type)
│   ├── POST   /                        (Create)
│   ├── PUT    /{id}                    (Update)
│   └── DELETE /{id}                    (Delete)
│
├── /deployments
│   ├── GET    /                                (List all)
│   ├── GET    /{id}                            (Get by ID)
│   ├── GET    /service/{serviceId}             (List by service)
│   ├── GET    /server/{serverId}               (List by server)
│   ├── GET    /status/{status}                 (List by status)
│   ├── GET    /environment/{env}               (List by environment)
│   ├── GET    /service/{id}/environment/{env}  (List by service & env)
│   ├── POST   /                                (Create)
│   ├── POST   /{id}/start                      (Start deployment)
│   ├── POST   /{id}/stop                       (Stop deployment)
│   ├── POST   /{id}/health                     (Update health)
│   ├── PUT    /{id}                            (Update)
│   └── DELETE /{id}                            (Delete)
│
└── /configurations
    ├── GET    /                                      (List all)
    ├── GET    /{id}                                  (Get by ID)
    ├── GET    /service/{serviceId}                   (List by service)
    ├── GET    /service/{id}/environment/{env}        (List by service & env)
    ├── GET    /service/{id}/key/{key}/environment/{env} (Get specific)
    ├── POST   /                                      (Create)
    ├── PUT    /{id}                                  (Update)
    └── DELETE /{id}                                  (Delete)
```

## Framework Categories

```
Java Frameworks
├── JAVA_SPRING      (Spring Boot)
├── JAVA_QUARKUS     (Quarkus)
└── JAVA_MICRONAUT   (Micronaut)

Node.js Frameworks
├── NODE_EXPRESS     (Express.js)
├── NODE_NESTJS      (NestJS)
├── NODE_ADONISJS    (AdonisJS)
└── NODE_MOLECULER   (Moleculer)

Python Frameworks
├── PYTHON_DJANGO    (Django)
├── PYTHON_FLASK     (Flask)
└── PYTHON_FASTAPI   (FastAPI)

Other Frameworks
├── DOTNET_ASPNET    (ASP.NET Core)
├── GO_GOA           (Gin)
├── RUST_ACTIX       (Actix)
└── OTHER            (Custom/Other)
```

## Service Types

```
API Services
├── REST_API         (RESTful services)
├── GRAPHQL_API      (GraphQL services)
└── GRPC_SERVICE     (gRPC services)

Infrastructure
├── GATEWAY          (API gateways)
├── PROXY            (Proxy services)
└── MESSAGE_QUEUE    (Message brokers)

Data Services
├── DATABASE         (Database services)
└── CACHE            (Cache services)

Applications
├── WEB_APP          (Web applications)
└── BACKGROUND_JOB   (Background workers)
```

## Deployment Status Flow

```
                    ┌─────────┐
                    │ STOPPED │
                    └────┬────┘
                         │
                         │ start()
                         ▼
                    ┌─────────┐
              ┌────►│STARTING │
              │     └────┬────┘
              │          │
              │          │ success
              │          ▼
              │     ┌─────────┐
              │     │ RUNNING │◄────┐
              │     └────┬────┘     │
              │          │          │
              │          │ stop()   │ restart
              │          ▼          │
              │     ┌─────────┐    │
              └─────│STOPPING │────┘
        failure     └────┬────┘
                         │
                         │ stopped
                         ▼
                    ┌─────────┐
                    │ STOPPED │
                    └─────────┘
                         │
                         │ error
                         ▼
                    ┌─────────┐
                    │ FAILED  │
                    └─────────┘
```

## Health Status States

```
┌─────────┐
│ UNKNOWN │  (Initial state, no health check yet)
└────┬────┘
     │
     │ health check performed
     ▼
┌─────────┐
│ HEALTHY │  (All checks passing)
└────┬────┘
     │
     │ partial failure
     ▼
┌─────────┐
│DEGRADED │  (Some checks failing)
└────┬────┘
     │
     │ complete failure
     ▼
┌──────────┐
│UNHEALTHY │  (All checks failing)
└──────────┘
```

## Integration Flow

```
┌─────────────┐
│   CI/CD     │
│  Pipeline   │
└──────┬──────┘
       │
       │ 1. Create Deployment
       ▼
┌─────────────┐
│ Host Server │
│     API     │
└──────┬──────┘
       │
       │ 2. Get Configuration
       ▼
┌─────────────┐
│   Service   │
│  Instance   │
└──────┬──────┘
       │
       │ 3. Register with Broker
       ▼
┌─────────────┐
│   Broker    │
│   Gateway   │
└──────┬──────┘
       │
       │ 4. Health Check
       ▼
┌─────────────┐
│ Monitoring  │
│   System    │
└─────────────┘
```

## Data Flow Example: Service Onboarding

```
1. Create Framework (if new)
   POST /api/frameworks
   {
     "name": "NestJS",
     "category": "NODE_NESTJS",
     "language": "TypeScript"
   }

2. Create Service
   POST /api/services
   {
     "name": "payment-service",
     "framework": {"id": 4},
     "type": "REST_API",
     "defaultPort": 8090
   }

3. Add Configurations
   POST /api/configurations
   {
     "service": {"id": 8},
     "configKey": "database.url",
     "configValue": "postgres://...",
     "environment": "DEVELOPMENT"
   }

4. Create Deployment
   POST /api/deployments
   {
     "service": {"id": 8},
     "server": {"id": 1},
     "port": 8090,
     "environment": "DEVELOPMENT"
   }

5. Start Deployment
   POST /api/deployments/5/start

6. Update Health
   POST /api/deployments/5/health?healthStatus=HEALTHY
```
