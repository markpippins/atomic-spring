package com.angrysurfer.atomic.registry.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angrysurfer.atomic.registry.model.ServiceRegistration;
import com.angrysurfer.atomic.registry.service.RegistryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/registry")
public class RegistryController {
    
    private final RegistryService registryService;
    
    public RegistryController(RegistryService registryService) {
        this.registryService = registryService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody ServiceRegistration registration) {
        registryService.register(registration);
        return ResponseEntity.ok(Map.of(
            "message", "Service registered successfully",
            "serviceName", registration.getServiceName()
        ));
    }
    
    @GetMapping("/services")
    public ResponseEntity<List<ServiceRegistration>> getAllServices() {
        return ResponseEntity.ok(registryService.getAllServices());
    }
    
    @GetMapping("/services/{serviceName}")
    public ResponseEntity<ServiceRegistration> getService(@PathVariable String serviceName) {
        return registryService.findByServiceName(serviceName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/operations/{operation}")
    public ResponseEntity<ServiceRegistration> getServiceByOperation(@PathVariable String operation) {
        return registryService.findByOperation(operation)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/services/{serviceName}")
    public ResponseEntity<Map<String, String>> deregister(@PathVariable String serviceName) {
        registryService.deregister(serviceName);
        return ResponseEntity.ok(Map.of("message", "Service deregistered successfully"));
    }
    
    @PostMapping("/heartbeat/{serviceName}")
    public ResponseEntity<Map<String, String>> heartbeat(@PathVariable String serviceName) {
        registryService.updateHeartbeat(serviceName);
        return ResponseEntity.ok(Map.of("message", "Heartbeat received"));
    }
}