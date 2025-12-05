package com.angrysurfer.atomic.hostserver.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angrysurfer.atomic.hostserver.dto.DeploymentWithBackendsDto;
import com.angrysurfer.atomic.hostserver.dto.ServiceBackendDto;
import com.angrysurfer.atomic.hostserver.entity.ServiceBackend;
import com.angrysurfer.atomic.hostserver.service.ServiceBackendService;

/**
 * REST API for managing service backend connections
 * 
 * Endpoints for the Angular admin console to:
 * - View backend connections for a deployment
 * - Add/remove backend connections
 * - Configure backend roles (PRIMARY, BACKUP, etc.)
 * - View which services consume a backend
 */
@RestController
@RequestMapping("/api/backends")
@CrossOrigin(origins = "*")
public class ServiceBackendController {
    
    @Autowired
    private ServiceBackendService serviceBackendService;
    
    /**
     * Get all backends for a specific deployment
     * 
     * Example: GET /api/backends/deployment/123
     * Returns: List of backends that deployment 123 uses
     */
    @GetMapping("/deployment/{deploymentId}")
    public ResponseEntity<List<ServiceBackendDto>> getBackendsForDeployment(@PathVariable Long deploymentId) {
        List<ServiceBackendDto> backends = serviceBackendService.getBackendsForDeployment(deploymentId);
        return ResponseEntity.ok(backends);
    }
    
    /**
     * Get all consumers (services using this deployment as a backend)
     * 
     * Example: GET /api/backends/consumers/123
     * Returns: List of services that use deployment 123 as a backend
     */
    @GetMapping("/consumers/{deploymentId}")
    public ResponseEntity<List<ServiceBackendDto>> getConsumersForDeployment(@PathVariable Long deploymentId) {
        List<ServiceBackendDto> consumers = serviceBackendService.getConsumersForDeployment(deploymentId);
        return ResponseEntity.ok(consumers);
    }
    
    /**
     * Get deployment with all backend connections
     * 
     * Example: GET /api/backends/deployment/123/details
     * Returns: Deployment info + backends + consumers
     */
    @GetMapping("/deployment/{deploymentId}/details")
    public ResponseEntity<DeploymentWithBackendsDto> getDeploymentWithBackends(@PathVariable Long deploymentId) {
        DeploymentWithBackendsDto dto = serviceBackendService.getDeploymentWithBackends(deploymentId);
        return ResponseEntity.ok(dto);
    }
    
    /**
     * Add a backend connection
     * 
     * Example: POST /api/backends
     * Body: {
     *   "serviceDeploymentId": 123,
     *   "backendDeploymentId": 456,
     *   "role": "PRIMARY",
     *   "priority": 1
     * }
     */
    @PostMapping
    public ResponseEntity<ServiceBackend> addBackend(@RequestBody Map<String, Object> request) {
        Long serviceDeploymentId = Long.valueOf(request.get("serviceDeploymentId").toString());
        Long backendDeploymentId = Long.valueOf(request.get("backendDeploymentId").toString());
        ServiceBackend.BackendRole role = ServiceBackend.BackendRole.valueOf(
            request.getOrDefault("role", "PRIMARY").toString()
        );
        Integer priority = request.containsKey("priority") ? 
            Integer.valueOf(request.get("priority").toString()) : 1;
        
        ServiceBackend backend = serviceBackendService.addBackend(
            serviceDeploymentId, backendDeploymentId, role, priority
        );
        
        return new ResponseEntity<>(backend, HttpStatus.CREATED);
    }
    
    /**
     * Update backend configuration
     * 
     * Example: PUT /api/backends/789
     * Body: {
     *   "role": "BACKUP",
     *   "priority": 2,
     *   "isActive": true
     * }
     */
    @PutMapping("/{backendId}")
    public ResponseEntity<ServiceBackend> updateBackend(
            @PathVariable Long backendId, 
            @RequestBody ServiceBackendDto dto) {
        ServiceBackend updated = serviceBackendService.updateBackend(backendId, dto);
        return ResponseEntity.ok(updated);
    }
    
    /**
     * Remove a backend connection
     * 
     * Example: DELETE /api/backends/789
     */
    @DeleteMapping("/{backendId}")
    public ResponseEntity<Void> removeBackend(@PathVariable Long backendId) {
        serviceBackendService.removeBackend(backendId);
        return ResponseEntity.noContent().build();
    }
}
