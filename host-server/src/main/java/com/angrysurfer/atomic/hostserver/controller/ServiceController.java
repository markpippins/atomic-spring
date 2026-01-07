package com.angrysurfer.atomic.hostserver.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.angrysurfer.atomic.hostserver.client.ServicesConsoleClient;
import com.angrysurfer.atomic.hostserver.entity.Service;
import com.angrysurfer.atomic.hostserver.repository.ServiceRepository;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*")
public class ServiceController {

    private static final Logger log = LoggerFactory.getLogger(ServiceController.class);

    private final ServicesConsoleClient client;
    private final ServiceRepository serviceRepository;

    public ServiceController(ServicesConsoleClient client, ServiceRepository serviceRepository) {
        this.client = client;
        this.serviceRepository = serviceRepository;
    }

    @GetMapping
    public List<Service> getAllServices() {
        log.info("Fetching all services from database");
        return serviceRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        log.info("Fetching service by ID: {}", id);
        Optional<Service> service = serviceRepository.findById(id);
        return service.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Service> getServiceByName(@PathVariable String name) {
        log.info("Fetching service by name: {}", name);
        Optional<Service> service = serviceRepository.findByName(name);
        return service.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/framework/{frameworkId}")
    public List<Service> getServicesByFramework(@PathVariable Long frameworkId) {
        log.info("Fetching services by framework ID: {}", frameworkId);
        return serviceRepository.findByFrameworkId(frameworkId);
    }

    @GetMapping("/{id}/dependencies")
    public ResponseEntity<List<Service>> getServiceDependencies(@PathVariable Long id) {
        log.info("Fetching dependencies for service: {}", id);
        // This would require a custom query or method in the repository
        // For now, returning empty list
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{id}/dependents")
    public ResponseEntity<List<Service>> getServiceDependents(@PathVariable String id) {
        log.info("Fetching dependents for service: {}", id);
        // This would require a custom query or method in the repository
        // For now, returning empty list
        return ResponseEntity.ok(List.of());
    }

    // Other specific filters (type, status) omitted or empty for now.

    // @Autowired
    // private com.angrysurfer.atomic.hostserver.service.ServiceSyncService
    // serviceSyncService;
    //
    // @PostMapping("/sync")
    // public ResponseEntity<Void> syncServices() {
    // log.info("Starting service synchronization");
    // try {
    // serviceSyncService.syncServices();
    // log.info("Service synchronization completed successfully");
    // return ResponseEntity.ok().build();
    // } catch (Exception e) {
    // log.error("Error during service synchronization", e);
    // throw e;
    // }
    // }
}