package com.angrysurfer.atomic.hostserver.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.angrysurfer.atomic.hostserver.entity.Service;
import com.angrysurfer.atomic.hostserver.repository.ServiceRepository;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*")
@SuppressWarnings("null")
public class ServiceController {

    private static final Logger log = LoggerFactory.getLogger(ServiceController.class);

    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping
    public List<Service> getAllServices() {
        log.info("Fetching all services");
        List<Service> services = serviceRepository.findAll();
        log.debug("Fetched {} services", services.size());
        return services;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        log.info("Fetching service by ID: {}", id);
        return serviceRepository.findById(id)
            .map(service -> {
                log.debug("Service found with ID: {}", id);
                return ResponseEntity.ok(service);
            })
            .orElseGet(() -> {
                log.warn("Service not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            });
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Service> getServiceByName(@PathVariable String name) {
        log.info("Fetching service by name: {}", name);
        return serviceRepository.findByName(name)
            .map(service -> {
                log.debug("Service found with name: {}", name);
                return ResponseEntity.ok(service);
            })
            .orElseGet(() -> {
                log.warn("Service not found with name: {}", name);
                return ResponseEntity.notFound().build();
            });
    }

    @GetMapping("/framework/{frameworkId}")
    public List<Service> getServicesByFramework(@PathVariable Long frameworkId) {
        log.info("Fetching services by framework ID: {}", frameworkId);
        List<Service> services = serviceRepository.findByFrameworkId(frameworkId);
        log.debug("Fetched {} services for framework ID: {}", services.size(), frameworkId);
        return services;
    }
    
    @Autowired
    private com.angrysurfer.atomic.hostserver.repository.ServiceTypeRepository serviceTypeRepository;

    @GetMapping("/type/{typeId}")
    public List<Service> getServicesByType(@PathVariable Long typeId) {
        log.info("Fetching services by type ID: {}", typeId);
        return serviceTypeRepository.findById(typeId)
                .map(type -> {
                    List<Service> services = serviceRepository.findByType(type);
                    log.debug("Fetched {} services for type ID: {}", services.size(), typeId);
                    return services;
                })
                .orElse(List.of());
    }

    @GetMapping("/status/{status}")
    public List<Service> getServicesByStatus(@PathVariable Service.ServiceStatus status) {
        log.info("Fetching services by status: {}", status);
        List<Service> services = serviceRepository.findByStatus(status);
        log.debug("Fetched {} services with status: {}", services.size(), status);
        return services;
    }
    
    @GetMapping("/{id}/dependencies")
    public ResponseEntity<List<Service>> getServiceDependencies(@PathVariable Long id) {
        log.info("Fetching dependencies for service ID: {}", id);
        return serviceRepository.findById(id)
            .map(service -> {
                List<Service> dependencies = List.copyOf(service.getDependencies());
                log.debug("Fetched {} dependencies for service ID: {}", dependencies.size(), id);
                return ResponseEntity.ok(dependencies);
            })
            .orElseGet(() -> {
                log.warn("Service not found with ID: {} when fetching dependencies", id);
                return ResponseEntity.notFound().build();
            });
    }

    @GetMapping("/{id}/dependents")
    public List<Service> getServiceDependents(@PathVariable Long id) {
        log.info("Fetching dependents for service ID: {}", id);
        List<Service> dependents = serviceRepository.findDependents(id);
        log.debug("Fetched {} dependents for service ID: {}", dependents.size(), id);
        return dependents;
    }
    
    @PostMapping
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        log.info("Creating service with name: {}", service.getName());
        try {
            Service savedService = serviceRepository.save(service);
            log.info("Service created successfully with ID: {}", savedService.getId());
            return new ResponseEntity<>(savedService, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating service: {}", service.getName(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable Long id, @RequestBody Service serviceDetails) {
        log.info("Updating service with ID: {}", id);
        return serviceRepository.findById(id)
            .map(service -> {
                service.setName(serviceDetails.getName());
                service.setDescription(serviceDetails.getDescription());
                service.setFramework(serviceDetails.getFramework());
                service.setType(serviceDetails.getType());
                service.setRepositoryUrl(serviceDetails.getRepositoryUrl());
                service.setVersion(serviceDetails.getVersion());
                service.setDefaultPort(serviceDetails.getDefaultPort());
                service.setHealthCheckPath(serviceDetails.getHealthCheckPath());
                service.setApiBasePath(serviceDetails.getApiBasePath());
                service.setStatus(serviceDetails.getStatus());
                Service updatedService = serviceRepository.save(service);
                log.info("Service updated successfully with ID: {}", updatedService.getId());
                return ResponseEntity.ok(updatedService);
            })
            .orElseGet(() -> {
                log.warn("Service not found with ID: {} for update", id);
                return ResponseEntity.notFound().build();
            });
    }
    
    @PostMapping("/{id}/dependencies/{dependencyId}")
    public ResponseEntity<Service> addDependency(@PathVariable Long id, @PathVariable Long dependencyId) {
        log.info("Adding dependency ID: {} to service ID: {}", dependencyId, id);
        return serviceRepository.findById(id)
            .flatMap(service -> serviceRepository.findById(dependencyId)
                .map(dependency -> {
                    service.getDependencies().add(dependency);
                    Service updatedService = serviceRepository.save(service);
                    log.info("Dependency added successfully - Service: {}, Dependency: {}", id, dependencyId);
                    return ResponseEntity.ok(updatedService);
                }))
            .orElseGet(() -> {
                log.warn("Either service ID: {} or dependency ID: {} not found", id, dependencyId);
                return ResponseEntity.notFound().build();
            });
    }

    @DeleteMapping("/{id}/dependencies/{dependencyId}")
    public ResponseEntity<Service> removeDependency(@PathVariable Long id, @PathVariable Long dependencyId) {
        log.info("Removing dependency ID: {} from service ID: {}", dependencyId, id);
        return serviceRepository.findById(id)
            .flatMap(service -> serviceRepository.findById(dependencyId)
                .map(dependency -> {
                    service.getDependencies().remove(dependency);
                    Service updatedService = serviceRepository.save(service);
                    log.info("Dependency removed successfully - Service: {}, Dependency: {}", id, dependencyId);
                    return ResponseEntity.ok(updatedService);
                }))
            .orElseGet(() -> {
                log.warn("Either service ID: {} or dependency ID: {} not found", id, dependencyId);
                return ResponseEntity.notFound().build();
            });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        log.info("Deleting service with ID: {}", id);
        return serviceRepository.findById(id)
            .map(service -> {
                serviceRepository.delete(service);
                log.info("Service deleted successfully with ID: {}", id);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElseGet(() -> {
                log.warn("Service not found with ID: {} for deletion", id);
                return ResponseEntity.notFound().build();
            });
    }
    @Autowired
    private com.angrysurfer.atomic.hostserver.service.ServiceSyncService serviceSyncService;

    @PostMapping("/sync")
    public ResponseEntity<Void> syncServices() {
        log.info("Starting service synchronization");
        try {
            serviceSyncService.syncServices();
            log.info("Service synchronization completed successfully");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error during service synchronization", e);
            throw e;
        }
    }
}