package com.angrysurfer.atomic.registry.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.angrysurfer.atomic.registry.model.ServiceRegistration;
import com.angrysurfer.atomic.registry.model.ServiceRegistration.ServiceStatus;

@Service
public class RegistryService {
    
    private static final Logger log = LoggerFactory.getLogger(RegistryService.class);
    
    // Map: serviceName -> ServiceRegistration
    private final Map<String, ServiceRegistration> registry = new ConcurrentHashMap<>();
    
    // Map: operation -> serviceName (for quick lookup)
    private final Map<String, String> operationIndex = new ConcurrentHashMap<>();
    
    public void register(ServiceRegistration registration) {
        registration.setLastHeartbeat(Instant.now());
        registration.setStatus(ServiceStatus.HEALTHY);
        
        // Store in registry
        registry.put(registration.getServiceName(), registration);
        
        // Index operations
        for (String operation : registration.getOperations()) {
            operationIndex.put(operation, registration.getServiceName());
        }
        
        log.info("Registered service: {} with operations: {}", 
                registration.getServiceName(), registration.getOperations());
    }
    
    public Optional<ServiceRegistration> findByServiceName(String serviceName) {
        return Optional.ofNullable(registry.get(serviceName));
    }
    
    public Optional<ServiceRegistration> findByOperation(String operation) {
        String serviceName = operationIndex.get(operation);
        if (serviceName != null) {
            return Optional.ofNullable(registry.get(serviceName));
        }
        return Optional.empty();
    }
    
    public List<ServiceRegistration> getAllServices() {
        return List.copyOf(registry.values());
    }
    
    public void deregister(String serviceName) {
        ServiceRegistration registration = registry.remove(serviceName);
        if (registration != null) {
            // Remove from operation index
            for (String operation : registration.getOperations()) {
                operationIndex.remove(operation);
            }
            log.info("Deregistered service: {}", serviceName);
        }
    }
    
    public void updateHeartbeat(String serviceName) {
        ServiceRegistration registration = registry.get(serviceName);
        if (registration != null) {
            registration.setLastHeartbeat(Instant.now());
            registration.setStatus(ServiceStatus.HEALTHY);
        }
    }
    
    public void markUnhealthy(String serviceName) {
        ServiceRegistration registration = registry.get(serviceName);
        if (registration != null) {
            registration.setStatus(ServiceStatus.UNHEALTHY);
            log.warn("Marked service as unhealthy: {}", serviceName);
        }
    }
}