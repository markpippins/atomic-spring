package com.angrysurfer.atomic.hostserver.controller;

import com.angrysurfer.atomic.hostserver.entity.Deployment;
import com.angrysurfer.atomic.hostserver.repository.DeploymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger log = LoggerFactory.getLogger(DeploymentController.class);
    
    @Autowired
    private DeploymentRepository deploymentRepository;
    
    @GetMapping
    public List<Deployment> getAllDeployments() {
        log.info("Fetching all deployments");
        List<Deployment> deployments = deploymentRepository.findAll();
        log.debug("Fetched {} deployments", deployments.size());
        return deployments;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Deployment> getDeploymentById(@PathVariable Long id) {
        log.info("Fetching deployment by id: {}", id);
        return deploymentRepository.findById(id)
            .map(deployment -> {
                log.debug("Found deployment: {}", deployment.getId());
                return ResponseEntity.ok(deployment);
            })
            .orElseGet(() -> {
                log.warn("Deployment not found with id: {}", id);
                return ResponseEntity.notFound().build();
            });
    }
    
    @GetMapping("/service/{serviceId}")
    public List<Deployment> getDeploymentsByService(@PathVariable Long serviceId) {
        log.info("Fetching deployments for service id: {}", serviceId);
        List<Deployment> deployments = deploymentRepository.findByServiceId(serviceId);
        log.debug("Found {} deployments for service {}", deployments.size(), serviceId);
        return deployments;
    }
    
    @GetMapping("/server/{serverId}")
    public List<Deployment> getDeploymentsByServer(@PathVariable Long serverId) {
        log.info("Fetching deployments for server id: {}", serverId);
        List<Deployment> deployments = deploymentRepository.findByServerId(serverId);
        log.debug("Found {} deployments for server {}", deployments.size(), serverId);
        return deployments;
    }
    
    @GetMapping("/status/{status}")
    public List<Deployment> getDeploymentsByStatus(@PathVariable Deployment.DeploymentStatus status) {
        log.info("Fetching deployments by status: {}", status);
        List<Deployment> deployments = deploymentRepository.findByStatus(status);
        log.debug("Found {} deployments with status {}", deployments.size(), status);
        return deployments;
    }
    
    @GetMapping("/environment/{environment}")
    public List<Deployment> getDeploymentsByEnvironment(@PathVariable Deployment.DeploymentEnvironment environment) {
        log.info("Fetching deployments by environment: {}", environment);
        List<Deployment> deployments = deploymentRepository.findByEnvironment(environment);
        log.debug("Found {} deployments in environment {}", deployments.size(), environment);
        return deployments;
    }
    
    @GetMapping("/service/{serviceId}/environment/{environment}")
    public List<Deployment> getDeploymentsByServiceAndEnvironment(
            @PathVariable Long serviceId, 
            @PathVariable Deployment.DeploymentEnvironment environment) {
        log.info("Fetching deployments for service {} in environment {}", serviceId, environment);
        List<Deployment> deployments = deploymentRepository.findByServiceIdAndEnvironment(serviceId, environment);
        log.debug("Found {} deployments for service {} in environment {}", deployments.size(), serviceId, environment);
        return deployments;
    }
    
    @PostMapping
    public ResponseEntity<Deployment> createDeployment(@RequestBody Deployment deployment) {
        log.info("Creating new deployment for service id: {}", deployment.getService() != null ? deployment.getService().getId() : "unknown");
        deployment.setDeployedAt(LocalDateTime.now());
        Deployment savedDeployment = deploymentRepository.save(deployment);
        log.debug("Created deployment with id: {}", savedDeployment.getId());
        return new ResponseEntity<>(savedDeployment, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Deployment> updateDeployment(@PathVariable Long id, @RequestBody Deployment deploymentDetails) {
        log.info("Updating deployment with id: {}", id);
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
                Deployment updated = deploymentRepository.save(deployment);
                log.debug("Updated deployment: {}", updated.getId());
                return ResponseEntity.ok(updated);
            })
            .orElseGet(() -> {
                log.warn("Deployment not found for update with id: {}", id);
                return ResponseEntity.notFound().build();
            });
    }
    
    @PostMapping("/{id}/start")
    public ResponseEntity<Deployment> startDeployment(@PathVariable Long id) {
        log.info("Starting deployment with id: {}", id);
        return deploymentRepository.findById(id)
            .map(deployment -> {
                deployment.setStatus(Deployment.DeploymentStatus.RUNNING);
                deployment.setStartedAt(LocalDateTime.now());
                Deployment started = deploymentRepository.save(deployment);
                log.debug("Started deployment: {}", started.getId());
                return ResponseEntity.ok(started);
            })
            .orElseGet(() -> {
                log.warn("Deployment not found for start with id: {}", id);
                return ResponseEntity.notFound().build();
            });
    }
    
    @PostMapping("/{id}/stop")
    public ResponseEntity<Deployment> stopDeployment(@PathVariable Long id) {
        log.info("Stopping deployment with id: {}", id);
        return deploymentRepository.findById(id)
            .map(deployment -> {
                deployment.setStatus(Deployment.DeploymentStatus.STOPPED);
                deployment.setStoppedAt(LocalDateTime.now());
                Deployment stopped = deploymentRepository.save(deployment);
                log.debug("Stopped deployment: {}", stopped.getId());
                return ResponseEntity.ok(stopped);
            })
            .orElseGet(() -> {
                log.warn("Deployment not found for stop with id: {}", id);
                return ResponseEntity.notFound().build();
            });
    }
    
    @PostMapping("/{id}/health")
    public ResponseEntity<Deployment> updateHealthStatus(
            @PathVariable Long id, 
            @RequestParam Deployment.HealthStatus healthStatus) {
        log.info("Updating health status for deployment id: {} to {}", id, healthStatus);
        return deploymentRepository.findById(id)
            .map(deployment -> {
                deployment.setHealthStatus(healthStatus);
                deployment.setLastHealthCheck(LocalDateTime.now());
                Deployment updated = deploymentRepository.save(deployment);
                log.debug("Updated health status for deployment: {}", updated.getId());
                return ResponseEntity.ok(updated);
            })
            .orElseGet(() -> {
                log.warn("Deployment not found for health update with id: {}", id);
                return ResponseEntity.notFound().build();
            });
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeployment(@PathVariable Long id) {
        log.info("Deleting deployment with id: {}", id);
        return deploymentRepository.findById(id)
            .map(deployment -> {
                deploymentRepository.delete(deployment);
                log.debug("Deleted deployment: {}", id);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElseGet(() -> {
                log.warn("Deployment not found for deletion with id: {}", id);
                return ResponseEntity.notFound().build();
            });
    }
}
