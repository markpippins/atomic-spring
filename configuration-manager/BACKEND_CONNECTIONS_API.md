# Service Backend Connections API

## Overview

The Service Backend Connections feature allows tracking relationships between service instances (deployments). This is essential for modeling scenarios like:

- **file-service** uses **file-system-server** for storage
- **file-service** uses multiple **file-system-server** instances (primary + backup)
- **broker-gateway** instances connect to different **file-system-server** instances

## Concepts

### Service vs Deployment vs Backend

```
Service (Template)
└── Deployments (Instances)
    └── Backends (Connections to other instances)
```

**Example**:
```
Service: file-system-server
├── Deployment 1: localhost:4040 (for broker-gateway-A)
├── Deployment 2: localhost:4041 (for broker-gateway-B)
└── Deployment 3: prod-server:4040 (for production)

Service: file-service
├── Deployment 1: localhost:8084
│   └── Backends:
│       ├── file-system-server (localhost:4040) - PRIMARY
│       └── file-system-server (localhost:4041) - BACKUP
└── Deployment 2: localhost:8094
    └── Backends:
        └── file-system-server (localhost:4042) - PRIMARY
```

### Backend Roles

| Role | Description | Use Case |
|------|-------------|----------|
| `PRIMARY` | Main backend | Primary data source |
| `BACKUP` | Failover backend | Used when primary fails |
| `ARCHIVE` | Cold storage | Old/archived data |
| `CACHE` | Hot cache layer | Fast access cache |
| `SHARD` | Data partition | Handles subset of data |
| `READ_REPLICA` | Read-only copy | Handles read queries |

## API Endpoints

Base URL: `http://localhost:8085/api/backends`

### 1. Get Backends for a Deployment

**Endpoint**: `GET /api/backends/deployment/{deploymentId}`

**Description**: Get all backends that a deployment uses

**Example Request**:
```bash
curl http://localhost:8085/api/backends/deployment/123
```

**Example Response**:
```json
[
  {
    "id": 1,
    "serviceDeploymentId": 123,
    "backendDeploymentId": 456,
    "role": "PRIMARY",
    "priority": 1,
    "routingKey": null,
    "weight": 100,
    "isActive": true,
    "description": "Primary file storage",
    "serviceDeploymentName": "file-service (localhost:8084)",
    "backendDeploymentName": "file-system-server (localhost:4040)",
    "backendStatus": "RUNNING"
  },
  {
    "id": 2,
    "serviceDeploymentId": 123,
    "backendDeploymentId": 457,
    "role": "BACKUP",
    "priority": 2,
    "routingKey": null,
    "weight": 100,
    "isActive": true,
    "description": "Backup file storage",
    "serviceDeploymentName": "file-service (localhost:8084)",
    "backendDeploymentName": "file-system-server (localhost:4041)",
    "backendStatus": "RUNNING"
  }
]
```

### 2. Get Consumers for a Deployment

**Endpoint**: `GET /api/backends/consumers/{deploymentId}`

**Description**: Get all services that use this deployment as a backend

**Example Request**:
```bash
curl http://localhost:8085/api/backends/consumers/456
```

**Example Response**:
```json
[
  {
    "id": 1,
    "serviceDeploymentId": 123,
    "backendDeploymentId": 456,
    "role": "PRIMARY",
    "priority": 1,
    "serviceDeploymentName": "file-service (localhost:8084)",
    "backendDeploymentName": "file-system-server (localhost:4040)",
    "backendStatus": "RUNNING"
  }
]
```

### 3. Get Deployment with All Connections

**Endpoint**: `GET /api/backends/deployment/{deploymentId}/details`

**Description**: Get deployment info with backends and consumers

**Example Request**:
```bash
curl http://localhost:8085/api/backends/deployment/123/details
```

**Example Response**:
```json
{
  "id": 123,
  "serviceName": "file-service",
  "serverHostname": "localhost",
  "port": 8084,
  "version": "1.0.0",
  "status": "RUNNING",
  "environment": "DEVELOPMENT",
  "backends": [
    {
      "id": 1,
      "role": "PRIMARY",
      "backendDeploymentName": "file-system-server (localhost:4040)",
      "backendStatus": "RUNNING"
    }
  ],
  "consumers": []
}
```

### 4. Add Backend Connection

**Endpoint**: `POST /api/backends`

**Description**: Create a new backend connection

**Example Request**:
```bash
curl -X POST http://localhost:8085/api/backends \
  -H "Content-Type: application/json" \
  -d '{
    "serviceDeploymentId": 123,
    "backendDeploymentId": 456,
    "role": "PRIMARY",
    "priority": 1
  }'
```

**Request Body**:
```json
{
  "serviceDeploymentId": 123,      // Required: The service using the backend
  "backendDeploymentId": 456,      // Required: The backend being used
  "role": "PRIMARY",                // Optional: Default is PRIMARY
  "priority": 1                     // Optional: Default is 1
}
```

**Response**: `201 Created` with the created ServiceBackend entity

### 5. Update Backend Connection

**Endpoint**: `PUT /api/backends/{backendId}`

**Description**: Update backend configuration

**Example Request**:
```bash
curl -X PUT http://localhost:8085/api/backends/1 \
  -H "Content-Type: application/json" \
  -d '{
    "role": "BACKUP",
    "priority": 2,
    "isActive": false
  }'
```

**Request Body** (all fields optional):
```json
{
  "role": "BACKUP",
  "priority": 2,
  "routingKey": "users-a-m",
  "weight": 50,
  "isActive": false,
  "description": "Backup storage for users A-M"
}
```

### 6. Remove Backend Connection

**Endpoint**: `DELETE /api/backends/{backendId}`

**Description**: Remove a backend connection

**Example Request**:
```bash
curl -X DELETE http://localhost:8085/api/backends/1
```

**Response**: `204 No Content`

## Frontend Integration

### TypeScript Models

```typescript
// models/service-backend.model.ts
export interface ServiceBackend {
  id: number;
  serviceDeploymentId: number;
  backendDeploymentId: number;
  role: BackendRole;
  priority: number;
  routingKey?: string;
  weight: number;
  isActive: boolean;
  description?: string;
  serviceDeploymentName: string;
  backendDeploymentName: string;
  backendStatus: string;
}

export enum BackendRole {
  PRIMARY = 'PRIMARY',
  BACKUP = 'BACKUP',
  ARCHIVE = 'ARCHIVE',
  CACHE = 'CACHE',
  SHARD = 'SHARD',
  READ_REPLICA = 'READ_REPLICA'
}

export interface DeploymentWithBackends {
  id: number;
  serviceName: string;
  serverHostname: string;
  port: number;
  version: string;
  status: string;
  environment: string;
  backends: ServiceBackend[];
  consumers: ServiceBackend[];
}
```

### Angular Service

```typescript
// services/backend.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BackendService {
  private apiUrl = 'http://localhost:8085/api/backends';

  constructor(private http: HttpClient) {}

  getBackendsForDeployment(deploymentId: number): Observable<ServiceBackend[]> {
    return this.http.get<ServiceBackend[]>(`${this.apiUrl}/deployment/${deploymentId}`);
  }

  getConsumersForDeployment(deploymentId: number): Observable<ServiceBackend[]> {
    return this.http.get<ServiceBackend[]>(`${this.apiUrl}/consumers/${deploymentId}`);
  }

  getDeploymentWithBackends(deploymentId: number): Observable<DeploymentWithBackends> {
    return this.http.get<DeploymentWithBackends>(`${this.apiUrl}/deployment/${deploymentId}/details`);
  }

  addBackend(request: AddBackendRequest): Observable<ServiceBackend> {
    return this.http.post<ServiceBackend>(this.apiUrl, request);
  }

  updateBackend(backendId: number, updates: Partial<ServiceBackend>): Observable<ServiceBackend> {
    return this.http.put<ServiceBackend>(`${this.apiUrl}/${backendId}`, updates);
  }

  removeBackend(backendId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${backendId}`);
  }
}

interface AddBackendRequest {
  serviceDeploymentId: number;
  backendDeploymentId: number;
  role?: string;
  priority?: number;
}
```

## UI Components to Build

### 1. Deployment Detail View

Show deployment with its backend connections:

```
┌─────────────────────────────────────────────────────────┐
│ Deployment: file-service (localhost:8084)               │
├─────────────────────────────────────────────────────────┤
│ Status: RUNNING                                          │
│ Version: 1.0.0                                           │
│ Environment: DEVELOPMENT                                 │
├─────────────────────────────────────────────────────────┤
│ Backends:                                                │
│ ┌─────────────────────────────────────────────────────┐ │
│ │ ✓ file-system-server (localhost:4040)              │ │
│ │   Role: PRIMARY | Priority: 1 | Status: RUNNING    │ │
│ │   [Edit] [Remove]                                   │ │
│ └─────────────────────────────────────────────────────┘ │
│ ┌─────────────────────────────────────────────────────┐ │
│ │ ✓ file-system-server (localhost:4041)              │ │
│ │   Role: BACKUP | Priority: 2 | Status: RUNNING     │ │
│ │   [Edit] [Remove]                                   │ │
│ └─────────────────────────────────────────────────────┘ │
│ [+ Add Backend]                                          │
└─────────────────────────────────────────────────────────┘
```

### 2. Add Backend Modal

```
┌─────────────────────────────────────────┐
│ Add Backend Connection                   │
├─────────────────────────────────────────┤
│ Backend Deployment:                      │
│ [Select Deployment ▼]                    │
│                                          │
│ Role:                                    │
│ [PRIMARY ▼]                              │
│                                          │
│ Priority:                                │
│ [1]                                      │
│                                          │
│ Description:                             │
│ [Optional description...]                │
│                                          │
│ [Cancel] [Add Backend]                   │
└─────────────────────────────────────────┘
```

### 3. Backend Status Badge

```typescript
// component
getRoleBadgeClass(role: string): string {
  const classes = {
    'PRIMARY': 'badge-primary',
    'BACKUP': 'badge-warning',
    'ARCHIVE': 'badge-secondary',
    'CACHE': 'badge-info',
    'SHARD': 'badge-success',
    'READ_REPLICA': 'badge-light'
  };
  return classes[role] || 'badge-secondary';
}
```

```html
<span [class]="'badge ' + getRoleBadgeClass(backend.role)">
  {{ backend.role }}
</span>
```

## Use Cases

### Use Case 1: View File Service Backends

1. Navigate to Deployments page
2. Click on file-service deployment
3. See "Backends" section showing file-system-server connections
4. See role (PRIMARY/BACKUP), status, and priority

### Use Case 2: Add Backup Storage

1. On file-service deployment detail page
2. Click "Add Backend"
3. Select file-system-server deployment from dropdown
4. Choose role: BACKUP
5. Set priority: 2
6. Click "Add Backend"
7. New backend appears in list

### Use Case 3: View Backend Consumers

1. Navigate to file-system-server deployment
2. See "Consumers" section
3. Shows which file-service instances use this backend
4. Shows role (PRIMARY/BACKUP) for each consumer

## Testing

### Test Scenario 1: Basic Backend Connection

```bash
# 1. Create deployments
POST /api/deployments
{
  "serviceId": 1,  // file-service
  "serverId": 1,   // localhost
  "port": 8084
}

POST /api/deployments
{
  "serviceId": 2,  // file-system-server
  "serverId": 1,
  "port": 4040
}

# 2. Add backend connection
POST /api/backends
{
  "serviceDeploymentId": 1,
  "backendDeploymentId": 2,
  "role": "PRIMARY"
}

# 3. Verify
GET /api/backends/deployment/1
# Should show file-system-server as backend

GET /api/backends/consumers/2
# Should show file-service as consumer
```

### Test Scenario 2: Multi-Backend Configuration

```bash
# Add primary backend
POST /api/backends
{
  "serviceDeploymentId": 1,
  "backendDeploymentId": 2,
  "role": "PRIMARY",
  "priority": 1
}

# Add backup backend
POST /api/backends
{
  "serviceDeploymentId": 1,
  "backendDeploymentId": 3,
  "role": "BACKUP",
  "priority": 2
}

# Verify both backends
GET /api/backends/deployment/1
# Should show 2 backends with different roles
```

## Future Enhancements

1. **Health Monitoring**: Automatically check backend health
2. **Failover Logic**: Auto-switch to backup when primary fails
3. **Load Balancing**: Distribute requests across multiple backends
4. **Sharding**: Route requests based on routing keys
5. **Metrics**: Track backend usage and performance