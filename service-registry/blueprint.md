# Service Registry Blueprint

## Overview
The Service Registry is a Spring Boot module responsible for tracking infrastructure components (Servers/Hosts), software frameworks, and service deployments across the atomic ecosystem. It provides a discovery mechanism and health monitoring for all registered services.

## Project Outline
- **Tech Stack**: Spring Boot 3, Spring Data JPA, MySQL, Redis (Caching), Jackson (JSON Serialization).
- **Core Entities**:
  - `Host`: Represents a physical or virtual server.
  - `Service`: Represents a software service defined by a framework and type.
  - `Framework`: Defines the technology stack (e.g., Spring Boot, NestJS).
  - `Deployment`: Tracks where a Service is running on a Host.
- **Data Initialization**: Automated preloading of system data from `src/main/resources/config/*.json`.

## Current Task: Refactor Data Model and Initialization logic

### 1. Data Model Refactor
The current model uses a mixture of `Long` ID fields and Entity objects for the same database columns, which is brittle.

**Steps**:
- [ ] Refactor `Framework` entity to remove `vendorId`, `categoryId`, `languageId` (Long) and use `@ManyToOne` relationship objects.
- [ ] Refactor `Service` entity to remove `frameworkId`, `serviceTypeId`.
- [ ] Refactor `Host` entity to remove `serverTypeId`, `environmentTypeId`, `operatingSystemId`.
- [ ] Refactor `Deployment` entity to remove `serviceId`, `serverId`, `environmentId`.
- [ ] Add `@Column(unique = true)` to `name` fields in lookup entities.

### 2. Initialization Routine Refactor
**Steps**:
- [ ] Update `DataInitializer` to resolve dependencies using Entity objects.
- [ ] Implement a lookup cache in `DataInitializer` to avoid redundant repository calls.
- [ ] Standardize the "Upsert" logic to prevent skipped growth.
