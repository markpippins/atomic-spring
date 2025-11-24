package com.angrysurfer.atomic.registry.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.angrysurfer.atomic.broker.spi.BrokerOperation;
import com.angrysurfer.atomic.broker.spi.BrokerParam;
import com.angrysurfer.atomic.broker.api.ServiceRegistration;
import com.angrysurfer.atomic.broker.api.ServiceRegistration.ServiceStatus;

@Service("serviceRegistry")
public class RegistryService {
    
    private static final Logger log = LoggerFactory.getLogger(RegistryService.class);
    
    // Map: serviceName -> ServiceRegistration
    private final Map<String, ServiceRegistration> registry = new ConcurrentHashMap<>();
    
    // Map: operation -> serviceName (for quick lookup)
    private final Map<String, String> operationIndex = new ConcurrentHashMap<>();
    
    @BrokerOperation("register")
    public Map<String, String> register(@BrokerParam("registration") ServiceRegistration registration) {
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
        
        return Map.of(
            "message", "Service registered successfully",
            "serviceName", registration.getServiceName()
        );
    }
    
    @BrokerOperation("findByServiceName")
    public ServiceRegistration findByServiceName(@BrokerParam("serviceName") String serviceName) {
        return registry.get(serviceName);
    }
    
    @BrokerOperation("findByOperation")
    public ServiceRegistration findByOperation(@BrokerParam("operation") String operation) {
        String serviceName = operationIndex.get(operation);
        if (serviceName != null) {
            return registry.get(serviceName);
        }
        return null;
    }
    
    @BrokerOperation("getAllServices")
    public List<ServiceRegistration> getAllServices() {
        return List.copyOf(registry.values());
    }
    
    @BrokerOperation("deregister")
    public Map<String, String> deregister(@BrokerParam("serviceName") String serviceName) {
        ServiceRegistration registration = registry.remove(serviceName);
        if (registration != null) {
            // Remove from operation index
            for (String operation : registration.getOperations()) {
                operationIndex.remove(operation);
            }
            log.info("Deregistered service: {}", serviceName);
            return Map.of("message", "Service deregistered successfully");
        }
        return Map.of("message", "Service not found");
    }
    
    @BrokerOperation("heartbeat")
    public Map<String, String> heartbeat(@BrokerParam("serviceName") String serviceName) {
        ServiceRegistration registration = registry.get(serviceName);
        if (registration != null) {
            registration.setLastHeartbeat(Instant.now());
            registration.setStatus(ServiceStatus.HEALTHY);
            return Map.of("message", "Heartbeat received");
        }
        return Map.of("message", "Service not found");
    }
    
    // Internal method for broker-gateway to query registry
    public Optional<ServiceRegistration> lookupByOperation(String operation) {
        String serviceName = operationIndex.get(operation);
        if (serviceName != null) {
            return Optional.ofNullable(registry.get(serviceName));
        }
        return Optional.empty();
    }
    
    public void markUnhealthy(String serviceName) {
        ServiceRegistration registration = registry.get(serviceName);
        if (registration != null) {
            registration.setStatus(ServiceStatus.UNHEALTHY);
            log.warn("Marked service as unhealthy: {}", serviceName);
        }
    }
}