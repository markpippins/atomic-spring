package com.angrysurfer.atomic.hostserver.controller;

import com.angrysurfer.atomic.hostserver.entity.Deployment;
import com.angrysurfer.atomic.hostserver.repository.DeploymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/deployments")
@CrossOrigin(origins = "*")
@SuppressWarnings("null")
public class DeploymentController {
    
    @Autowired
    private DeploymentRepository deploymentRepository;
    
    @GetMapping
    public List<Deployment> getAllDeployments() {
        return deploymentRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Deployment> getDeploymentById(@PathVariable Long id) {
        return deploymentRepository.findById(id)
            .map(deployment -> ResponseEntity.ok(deployment))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/service/{serviceId}")
    public List<Deployment> getDeploymentsByService(@PathVariable Long serviceId) {
        return deploymentRepository.findByServiceId(serviceId);
    }
    
    @GetMapping("/server/{serverId}")
    public List<Deployment> getDeploymentsByServer(@PathVariable Long serverId) {
        return deploymentRepository.findByServerId(serverId);
    }
    
    @GetMapping("/status/{status}")
    public List<Deployment> getDeploymentsByStatus(@PathVariable Deployment.DeploymentStatus status) {
        return deploymentRepository.findByStatus(status);
    }
    
    @GetMapping("/environment/{environment}")
    public List<Deployment> getDeploymentsByEnvironment(@PathVariable Deployment.DeploymentEnvironment environment) {
        return deploymentRepository.findByEnvironment(environment);
    }
    
    @GetMapping("/service/{serviceId}/environment/{environment}")
    public List<Deployment> getDeploymentsByServiceAndEnvironment(
            @PathVariable Long serviceId, 
            @PathVariable Deployment.DeploymentEnvironment environment) {
        return deploymentRepository.findByServiceIdAndEnvironment(serviceId, environment);
    }
    
    @PostMapping
    public ResponseEntity<Deployment> createDeployment(@RequestBody Deployment deployment) {
        deployment.setDeployedAt(LocalDateTime.now());
        Deployment savedDeployment = deploymentRepository.save(deployment);
        return new ResponseEntity<>(savedDeployment, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Deployment> updateDeployment(@PathVariable Long id, @RequestBody Deployment deploymentDetails) {
        return deploymentRepository.findById(id)
            .map(deployment -> {
                deployment.setPort(deploymentDetails.getPort());
                deployment.setContextPath(deploymentDetails.getContextPath());
                deployment.setVersion(deploymentDetails.getVersion());
                deployment.setStatus(deploymentDetails.getStatus());
                deployment.setEnvironment(deploymentDetails.getEnvironment());
                deployment.setHealthCheckUrl(deploymentDetails.getHealthCheckUrl());
                deployment.setHealthStatus(deploymentDetails.getHealthStatus());
                deployment.setProcessId(deploymentDetails.getProcessId());
                deployment.setContainerName(deploymentDetails.getContainerName());
                deployment.setDeploymentPath(deploymentDetails.getDeploymentPath());
                return ResponseEntity.ok(deploymentRepository.save(deployment));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/start")
    public ResponseEntity<Deployment> startDeployment(@PathVariable Long id) {
        return deploymentRepository.findById(id)
            .map(deployment -> {
                deployment.setStatus(Deployment.DeploymentStatus.RUNNING);
                deployment.setStartedAt(LocalDateTime.now());
                return ResponseEntity.ok(deploymentRepository.save(deployment));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/stop")
    public ResponseEntity<Deployment> stopDeployment(@PathVariable Long id) {
        return deploymentRepository.findById(id)
            .map(deployment -> {
                deployment.setStatus(Deployment.DeploymentStatus.STOPPED);
                deployment.setStoppedAt(LocalDateTime.now());
                return ResponseEntity.ok(deploymentRepository.save(deployment));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/health")
    public ResponseEntity<Deployment> updateHealthStatus(
            @PathVariable Long id, 
            @RequestParam Deployment.HealthStatus healthStatus) {
        return deploymentRepository.findById(id)
            .map(deployment -> {
                deployment.setHealthStatus(healthStatus);
                deployment.setLastHealthCheck(LocalDateTime.now());
                return ResponseEntity.ok(deploymentRepository.save(deployment));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeployment(@PathVariable Long id) {
        return deploymentRepository.findById(id)
            .map(deployment -> {
                deploymentRepository.delete(deployment);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
