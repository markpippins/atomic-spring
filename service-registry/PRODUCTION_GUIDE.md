# Production Deployment Guide

## Overview

This guide covers migrating the Host Server from development (H2 in-memory) to production with a persistent database, security, and monitoring.

## Phase 1: Database Migration

### Option A: PostgreSQL

#### 1. Add PostgreSQL Dependency

Update `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

#### 2. Update application.properties

```properties
# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/hostserver
spring.datasource.username=hostserver_user
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Disable H2 Console
spring.h2.console.enabled=false
```

### Option B: MySQL

#### 1. Add MySQL Dependency

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

#### 2. Update application.properties

```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/hostserver?useSSL=true&serverTimezone=UTC
spring.datasource.username=hostserver_user
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
```

### 3. Add Flyway for Schema Management

#### Add Dependency

```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```

#### Create Migration Scripts

Create `src/main/resources/db/migration/V1__initial_schema.sql`:

```sql
-- Frameworks
CREATE TABLE frameworks (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(1000),
    category VARCHAR(50) NOT NULL,
    language VARCHAR(100),
    latest_version VARCHAR(50),
    documentation_url VARCHAR(500),
    repository_url VARCHAR(500),
    supports_broker_pattern BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Servers
CREATE TABLE servers (
    id BIGSERIAL PRIMARY KEY,
    hostname VARCHAR(255) NOT NULL UNIQUE,
    ip_address VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    environment VARCHAR(50) NOT NULL,
    operating_system VARCHAR(100),
    cpu_cores INTEGER,
    memory_mb BIGINT,
    disk_gb BIGINT,
    region VARCHAR(100),
    cloud_provider VARCHAR(100),
    status VARCHAR(50) NOT NULL,
    description VARCHAR(1000),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Services
CREATE TABLE services (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(1000),
    framework_id BIGINT REFERENCES frameworks(id),
    type VARCHAR(50) NOT NULL,
    repository_url VARCHAR(500),
    version VARCHAR(50),
    default_port INTEGER,
    health_check_path VARCHAR(255),
    api_base_path VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Service Dependencies
CREATE TABLE service_dependencies (
    service_id BIGINT REFERENCES services(id) ON DELETE CASCADE,
    depends_on_id BIGINT REFERENCES services(id) ON DELETE CASCADE,
    PRIMARY KEY (service_id, depends_on_id)
);

-- Deployments
CREATE TABLE deployments (
    id BIGSERIAL PRIMARY KEY,
    service_id BIGINT NOT NULL REFERENCES services(id) ON DELETE CASCADE,
    server_id BIGINT NOT NULL REFERENCES servers(id) ON DELETE CASCADE,
    port INTEGER NOT NULL,
    context_path VARCHAR(255),
    version VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    environment VARCHAR(50) NOT NULL,
    health_check_url VARCHAR(500),
    last_health_check TIMESTAMP,
    health_status VARCHAR(50),
    process_id VARCHAR(100),
    container_name VARCHAR(255),
    deployment_path VARCHAR(500),
    deployed_at TIMESTAMP,
    started_at TIMESTAMP,
    stopped_at TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Service Configurations
CREATE TABLE service_configurations (
    id BIGSERIAL PRIMARY KEY,
    service_id BIGINT NOT NULL REFERENCES services(id) ON DELETE CASCADE,
    config_key VARCHAR(255) NOT NULL,
    config_value VARCHAR(4000),
    environment VARCHAR(50) NOT NULL,
    type VARCHAR(50) NOT NULL,
    is_secret BOOLEAN DEFAULT FALSE,
    description VARCHAR(1000),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Indexes
CREATE INDEX idx_services_framework ON services(framework_id);
CREATE INDEX idx_services_type ON services(type);
CREATE INDEX idx_services_status ON services(status);
CREATE INDEX idx_deployments_service ON deployments(service_id);
CREATE INDEX idx_deployments_server ON deployments(server_id);
CREATE INDEX idx_deployments_status ON deployments(status);
CREATE INDEX idx_deployments_environment ON deployments(environment);
CREATE INDEX idx_configurations_service ON service_configurations(service_id);
CREATE INDEX idx_configurations_environment ON service_configurations(environment);
```

#### Update application.properties

```properties
# Flyway Configuration
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.jpa.hibernate.ddl-auto=validate
```

## Phase 2: Security

### 1. Add Spring Security

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

### 2. Create Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/api/**").authenticated()
            )
            .httpBasic();
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### 3. Add User Management

Create User entity and UserDetailsService implementation for authentication.

### 4. Update CORS Configuration

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "https://your-domain.com",
            "https://admin.your-domain.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

## Phase 3: Secrets Management

### Option A: HashiCorp Vault

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-vault-config</artifactId>
</dependency>
```

```properties
spring.cloud.vault.uri=https://vault.your-domain.com
spring.cloud.vault.token=${VAULT_TOKEN}
spring.cloud.vault.kv.enabled=true
```

### Option B: AWS Secrets Manager

```xml
<dependency>
    <groupId>com.amazonaws.secretsmanager</groupId>
    <artifactId>aws-secretsmanager-jdbc</artifactId>
    <version>1.0.8</version>
</dependency>
```

### Option C: Environment Variables

```properties
spring.datasource.password=${DB_PASSWORD}
api.secret.key=${API_SECRET_KEY}
```

## Phase 4: Monitoring

### 1. Add Actuator Endpoints

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```properties
# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=when-authorized
management.metrics.export.prometheus.enabled=true
```

### 2. Add Prometheus

```xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### 3. Add Logging

```xml
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>7.4</version>
</dependency>
```

Create `logback-spring.xml`:

```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/service-registry.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/service-registry-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

## Phase 5: Performance

### 1. Add Connection Pooling

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
</dependency>
```

```properties
# HikariCP Configuration
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

### 2. Add Caching

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.create(connectionFactory);
    }
}
```

Add caching to repositories:

```java
@Cacheable("frameworks")
public List<Framework> findAll() {
    return frameworkRepository.findAll();
}

@CacheEvict(value = "frameworks", allEntries = true)
public Framework save(Framework framework) {
    return frameworkRepository.save(framework);
}
```

## Phase 6: Deployment

### Docker

Create `Dockerfile`:

```dockerfile
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/service-registry-*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: hostserver
      POSTGRES_USER: hostserver_user
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
  
  service-registry:
    build: .
    ports:
      - "8085:8085"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/hostserver
      SPRING_DATASOURCE_USERNAME: hostserver_user
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      SPRING_REDIS_HOST: redis
    depends_on:
      - postgres
      - redis

volumes:
  postgres_data:
```

### Kubernetes

Create `k8s/deployment.yaml`:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: service-registry
spec:
  replicas: 3
  selector:
    matchLabels:
      app: service-registry
  template:
    metadata:
      labels:
        app: service-registry
    spec:
      containers:
      - name: service-registry
        image: your-registry/service-registry:latest
        ports:
        - containerPort: 8085
        env:
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            secretKeyRef:
              name: service-registry-secrets
              key: database-url
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: service-registry-secrets
              key: database-password
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8085
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8085
          initialDelaySeconds: 20
          periodSeconds: 5
---
apiVersion: v1
kind: Service
metadata:
  name: service-registry
spec:
  selector:
    app: service-registry
  ports:
  - port: 8085
    targetPort: 8085
  type: LoadBalancer
```

## Phase 7: CI/CD

### GitHub Actions

Create `.github/workflows/deploy.yml`:

```yaml
name: Deploy Host Server

on:
  push:
    branches: [main]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 21
      uses: actions/setup-java@v3
      with:
        java-version: '21'
        distribution: 'temurin'
    
    - name: Build with Maven
      run: mvn clean package -DskipTests
      working-directory: spring/service-registry
    
    - name: Build Docker image
      run: docker build -t your-registry/service-registry:${{ github.sha }} .
      working-directory: spring/service-registry
    
    - name: Push to registry
      run: docker push your-registry/service-registry:${{ github.sha }}
    
    - name: Deploy to Kubernetes
      run: |
        kubectl set image deployment/service-registry \
          service-registry=your-registry/service-registry:${{ github.sha }}
```

## Checklist

### Pre-Production
- [ ] Migrate to persistent database
- [ ] Add Flyway migrations
- [ ] Implement authentication
- [ ] Configure CORS properly
- [ ] Set up secrets management
- [ ] Add monitoring and logging
- [ ] Configure connection pooling
- [ ] Add caching layer
- [ ] Create Docker image
- [ ] Write deployment scripts

### Production
- [ ] Set up database backups
- [ ] Configure SSL/TLS
- [ ] Set up monitoring alerts
- [ ] Configure log aggregation
- [ ] Set up CI/CD pipeline
- [ ] Document runbooks
- [ ] Create disaster recovery plan
- [ ] Set up rate limiting
- [ ] Configure auto-scaling
- [ ] Perform load testing

### Post-Production
- [ ] Monitor performance metrics
- [ ] Review security logs
- [ ] Optimize database queries
- [ ] Update documentation
- [ ] Train operations team
- [ ] Set up on-call rotation
- [ ] Review and update SLAs
- [ ] Plan capacity upgrades
