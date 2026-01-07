package com.angrysurfer.atomic.hostserver.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
