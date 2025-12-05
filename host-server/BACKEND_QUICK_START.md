# Backend Connections Quick Start

## Quick Example

```bash
# Get backends for deployment
curl http://localhost:8085/api/backends/deployment/123

# Add backend
curl -X POST http://localhost:8085/api/backends \
  -H "Content-Type: application/json" \
  -d '{"serviceDeploymentId": 123, "backendDeploymentId": 456, "role": "PRIMARY"}'
```

## Frontend Checklist

- [ ] Create `ServiceBackend` model
- [ ] Create `BackendService` 
- [ ] Add backends section to deployment detail page
- [ ] Add "Add Backend" modal

## API Endpoints

- `GET /api/backends/deployment/{id}` - Get backends
- `POST /api/backends` - Add backend
- `DELETE /api/backends/{id}` - Remove backend

See `BACKEND_CONNECTIONS_API.md` for full docs.