package com.angrysurfer.atomic.hostserver.controller;

import java.util.List;

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
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @GetMapping
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Service> getServiceById(@PathVariable Long id) {
        return serviceRepository.findById(id)
            .map(service -> ResponseEntity.ok(service))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<Service> getServiceByName(@PathVariable String name) {
        return serviceRepository.findByName(name)
            .map(service -> ResponseEntity.ok(service))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/framework/{frameworkId}")
    public List<Service> getServicesByFramework(@PathVariable Long frameworkId) {
        return serviceRepository.findByFrameworkId(frameworkId);
    }
    
    @Autowired
    private com.angrysurfer.atomic.hostserver.repository.ServiceTypeRepository serviceTypeRepository;

    @GetMapping("/type/{typeId}")
    public List<Service> getServicesByType(@PathVariable Long typeId) {
        return serviceTypeRepository.findById(typeId)
                .map(type -> serviceRepository.findByType(type))
                .orElse(List.of());
    }
    
    @GetMapping("/status/{status}")
    public List<Service> getServicesByStatus(@PathVariable Service.ServiceStatus status) {
        return serviceRepository.findByStatus(status);
    }
    
    @GetMapping("/{id}/dependencies")
    public ResponseEntity<List<Service>> getServiceDependencies(@PathVariable Long id) {
        return serviceRepository.findById(id)
            .map(service -> ResponseEntity.ok(List.copyOf(service.getDependencies())))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{id}/dependents")
    public List<Service> getServiceDependents(@PathVariable Long id) {
        return serviceRepository.findDependents(id);
    }
    
    @PostMapping
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        Service savedService = serviceRepository.save(service);
        return new ResponseEntity<>(savedService, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable Long id, @RequestBody Service serviceDetails) {
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
                return ResponseEntity.ok(serviceRepository.save(service));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping("/{id}/dependencies/{dependencyId}")
    public ResponseEntity<Service> addDependency(@PathVariable Long id, @PathVariable Long dependencyId) {
        return serviceRepository.findById(id)
            .flatMap(service -> serviceRepository.findById(dependencyId)
                .map(dependency -> {
                    service.getDependencies().add(dependency);
                    return ResponseEntity.ok(serviceRepository.save(service));
                }))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}/dependencies/{dependencyId}")
    public ResponseEntity<Service> removeDependency(@PathVariable Long id, @PathVariable Long dependencyId) {
        return serviceRepository.findById(id)
            .flatMap(service -> serviceRepository.findById(dependencyId)
                .map(dependency -> {
                    service.getDependencies().remove(dependency);
                    return ResponseEntity.ok(serviceRepository.save(service));
                }))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        return serviceRepository.findById(id)
            .map(service -> {
                serviceRepository.delete(service);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
    @Autowired
    private com.angrysurfer.atomic.hostserver.service.ServiceSyncService serviceSyncService;

    @PostMapping("/sync")
    public ResponseEntity<Void> syncServices() {
        serviceSyncService.syncServices();
        return ResponseEntity.ok().build();
    }
}