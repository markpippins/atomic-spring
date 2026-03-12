# Backend Connections Quick Start

## TL;DR

Service instances (deployments) can connect to other service instances as backends. Example: file-service uses file-system-server for storage.

## Quick Example

```bash
# 1. Get backends for file-service deployment
curl http://localhost:8085/api/backends/deployment/123

# 2. Add a backend connection
curl -X POST http://localhost:8085/api/backends \
  -H "Content-Type: application/json" \
  -d '{
    "serviceDeploymentId": 123,
    "backendDeploymentId": 456,
    "role": "PRIMARY"
  }'

# 3. See who uses file-system-server
curl http://localhost:8085/api/backends/consumers/456
```

## Frontend Checklist

### Models to Create
- [ ] `ServiceBackend` interface
- [ ] `BackendRole` enum
- [ ] `DeploymentWithBackends` interface

### Service to Create
- [ ] `BackendService` with methods:
  - `getBackendsForDeployment()`
  - `getConsumd`S_API.CTIONND_CONNEACKEtation: `Bumenull doc
See fons?
## Questi
```

]
}}
         }
  
        }"
 RY\"\n}: \"PRIMAole\""r,\n  \": 456tId\endDeploymen\"back\n  d\": 123,mentIployeDe \"servic{\n raw": ""
          "raw",":  "mode{
         ": dy      "bonds",
  /api/backe85ocalhost:80 "http://ll":    "ur
    "POST",thod":      "me": {
   "request,
      Backend"Add e": "  "nam
      { },
     }
     Id}}"
 ploymentnt/{{de/deploymekendsapi/bac085/host:8http://local": "url   "
     : "GET",  "method"      quest": {
      "reends",
t Back "Ge"name":  {
      em": [
    "it
  },
tions API"nnec"Backend Coname":    "": {
 "info`json
{
  

``tionsend Connection: Back# Collecostman

##ting in P

## Tes
```2)ority: RIMARY, prirver (Pm-se-syste└── fileiority: 1)
E, prhe (CACH─ redis-cac├─le-service
fi
```
 Storagehe +rn 3: Cac# Patte

##
```sers-n-z")Key: "uRD, routinger-2 (SHA-servsystem
└── file--a-m") "usersey:D, routingK1 (SHARrver--system-sefilee
├── 
file-servicng
```diSharPattern 2: 

### 
```ty: 2)CKUP, priori-2 (BAtem-serversysle- 1)
└── fi priority:(PRIMARY,-1 rverle-system-se
├── firvice-seile
```
fry + Backup1: Prima# Pattern rns

##atteCommon P## 

-only copyICA**: ReadPL*READ_REon
- *artitiRD**: Data phe
- **SHAt cac: HoHE**- **CACge
storaold RCHIVE**: C- **And
kever bac: FailoACKUP**t)
- **Bdefaulnd ( backeRY**: Main

- **PRIMAckend Roles |

## Baectionkend connacove b| Remends/{id}` /api/backDELETE | `
| nd config |date backe/{id}` | Up/backends`/apiUT | 
| Pnection |d conkendd bacnds` | AbackeST | `/api/PO |
| yment infot full deploGe | details`id}/oyment/{/deplpi/backends
| GET | `/ament |f deploy o consumers` | Getrs/{id}consumends/`/api/backe | | GETment |
 for deployt backendsGed}` | nt/{ieploymes/d/api/backend|
| GET | `--|-------------------------|pose |
|Purnt | dpoi | EnodMethummary

| dpoints SEn

## API ackend
```s as bng thisirvices u of seist └── L    Section
 Consumers
└──nsRemove actio── Edit/│   └d button
 backen Add
│   ├──f backendsList o  ├── ction
│ s Se
├── Backendatus)port, stme, ic Info (naBas├──  Page
illoyment Deta
Deps
```# UI Element

##component role badge  [ ] Backend
- component list Backend- [ ]odal
ckend md ba
- [ ] Ads sectionackend b - showpageil ment detaoy [ ] Depl
-ate/Updateents to Creompon
### Cckend()`
oveBaremnd()`
  - `eBackeat - `upd
 ckend()``addBa`
  - loyment()mersForDep