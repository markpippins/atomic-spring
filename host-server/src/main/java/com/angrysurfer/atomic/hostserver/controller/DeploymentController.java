package com.angrysurfer.atomic.hostserver.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.angrysurfer.atomic.hostserver.client.ServicesConsoleClient;
import com.angrysurfer.atomic.hostserver.entity.Deployment;
import com.angrysurfer.atomic.hostserver.repository.DeploymentRepository;

@RestController
@RequestMapping("/api/deployments")
@CrossOrigin(origins = "*")
public class DeploymentController {

    private static final Logger log = LoggerFactory.getLogger(DeploymentController.class);
    private final ServicesConsoleClient client;
    private final DeploymentRepository deploymentRepository;

    public DeploymentController(ServicesConsoleClient client, DeploymentRepository deploymentRepository) {
        this.client = client;
        this.deploymentRepository = deploymentRepository;
    }

    @GetMapping
    public List<Deployment> getAllDeployments() {
        log.info("Fetching all deployments from database");
        return deploymentRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Deployment> getDeploymentById(@PathVariable Long id) {
        log.info("Fetching deployment by id: {}", id);
        return deploymentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/service/{serviceId}")
    public List<Deployment> getDeploymentsByService(@PathVariable Long serviceId) {
        log.info("Fetching deployments for service: {}", serviceId);
        return deploymentRepository.findByServiceId(serviceId);
    }

    @PostMapping
    public ResponseEntity<Deployment> createDeployment(@RequestBody Deployment deployment) {
        log.info("Creating new deployment for service ID: {}", deployment.getServiceId());

        // Set active flag
        deployment.setActiveFlag(true);

        Deployment savedDeployment = deploymentRepository.save(deployment);
        log.info("Successfully created deployment with ID: {}", savedDeployment.getId());
        return ResponseEntity.ok(savedDeployment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Deployment> updateDeployment(@PathVariable Long id, @RequestBody Deployment deployment) {
        log.info("Updating deployment with ID: {}", id);

        Optional<Deployment> existingDeploymentOpt = deploymentRepository.findById(id);
        if (existingDeploymentOpt.isEmpty()) {
            log.warn("Deployment with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        // Update the deployment
        deployment.setId(id);
        Deployment updatedDeployment = deploymentRepository.save(deployment);
        log.info("Successfully updated deployment with ID: {}", id);
        return ResponseEntity.ok(updatedDeployment);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Deployment> updateDeploymentStatus(@PathVariable Long id, @RequestParam String status) {
        log.info("Updating deployment {} status to: {}", id, status);

        Optional<Deployment> deploymentOpt = deploymentRepository.findById(id);
        if (deploymentOpt.isEmpty()) {
            log.warn("Deployment with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        Deployment deployment = deploymentOpt.get();
        deployment.setStatus(status);
        Deployment updatedDeployment = deploymentRepository.save(deployment);
        log.info("Successfully updated deployment status");
        return ResponseEntity.ok(updatedDeployment);
    }

    @PatchMapping("/{id}/health")
    public ResponseEntity<Deployment> updateDeploymentHealth(@PathVariable Long id, @RequestParam String healthStatus) {
        log.info("Updating deployment {} health to: {}", id, healthStatus);

        Optional<Deployment> deploymentOpt = deploymentRepository.findById(id);
        if (deploymentOpt.isEmpty()) {
            log.warn("Deployment with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        Deployment deployment = deploymentOpt.get();
        deployment.setHealthStatus(healthStatus);
        Deployment updatedDeployment = deploymentRepository.save(deployment);
        log.info("Successfully updated deployment health status");
        return ResponseEntity.ok(updatedDeployment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeployment(@PathVariable Long id) {
        log.info("Deleting deployment with ID: {}", id);

        Optional<Deployment> deploymentOpt = deploymentRepository.findById(id);
        if (deploymentOpt.isEmpty()) {
            log.warn("Deployment with ID {} not found", id);
            return ResponseEntity.notFound().build();
        }

        deploymentRepository.deleteById(id);
        log.info("Successfully deleted deployment with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
