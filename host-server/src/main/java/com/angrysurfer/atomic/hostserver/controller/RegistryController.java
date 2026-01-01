package com.angrysurfer.atomic.hostserver.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angrysurfer.atomic.hostserver.dto.ExternalServiceRegistration;
import com.angrysurfer.atomic.hostserver.entity.Service;
import com.angrysurfer.atomic.hostserver.service.ExternalServiceRegistrationService;

@RestController
@RequestMapping("/api/registry")
@CrossOrigin(origins = "*")
public class RegistryController {
    
    private static final Logger log = LoggerFactory.getLogger(RegistryController.class);
    
    @Autowired
    private ExternalServiceRegistrationService registrationService;
    
    /**
     * Register an external service (e.g., Moleculer, Python, Go services)
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody ExternalServiceRegistration registration) {
        log.info("Received registration request for service: {}", registration.getServiceName());
        
        try {
            Service service = registrationService.registerExternalService(registration);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Service registered successfully",
                "serviceName", service.getName(),
                "serviceId", service.getId()
            ));
        } catch (Exception e) {
            log.error("Failed to register service: {}", registration.getServiceName(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to register service: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Heartbeat endpoint for external services to maintain registration
     */
    @PostMapping("/heartbeat/{serviceName}")
    public ResponseEntity<Map<String, String>> heartbeat(@PathVariable String serviceName) {
        log.debug("Received heartbeat from service: {}", serviceName);
        
        boolean updated = registrationService.updateHeartbeat(serviceName);
        
        if (updated) {
            return ResponseEntity.ok(Map.of(
                "message", "Heartbeat received",
                "serviceName", serviceName
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all registered services (for broker-gateway to query)
     */
    @GetMapping("/services")
    public ResponseEntity<List<Service>> getAllRegisteredServices() {
        List<Service> services = registrationService.getAllActiveServices();
        return ResponseEntity.ok(services);
    }
    
    /**
     * Find service by operation name (for broker-gateway routing)
     */
    @GetMapping("/services/by-operation/{operation}")
    public ResponseEntity<Service> findServiceByOperation(@PathVariable String operation) {
        return registrationService.findServiceByOperation(operation)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get service details with endpoint URL for direct calls
     */
    @GetMapping("/services/{serviceName}/details")
    public ResponseEntity<Map<String, Object>> getServiceDetails(@PathVariable String serviceName) {
        return registrationService.getServiceDetails(serviceName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Deregister a service
     */
    @PostMapping("/deregister/{serviceName}")
    public ResponseEntity<Map<String, String>> deregister(@PathVariable String serviceName) {
        log.info("Deregistering service: {}", serviceName);
        
        boolean removed = registrationService.deregisterService(serviceName);
        
        if (removed) {
            return ResponseEntity.ok(Map.of(
                "message", "Service deregistered successfully",
                "serviceName", serviceName
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}