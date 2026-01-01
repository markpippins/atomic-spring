package com.angrysurfer.atomic.broker.gateway.service;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ServiceDiscoveryClient {

    private static final Logger log = LoggerFactory.getLogger(ServiceDiscoveryClient.class);

    @Value("${host.server.url:http://localhost:8085}")
    private String hostServerUrl;

    @Autowired
    private RestTemplate restTemplate;

    // For testing purposes
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // For testing purposes
    public void setHostServerUrl(String hostServerUrl) {
        this.hostServerUrl = hostServerUrl;
    }

    /**
     * Find service that can handle the specified operation
     */
    public Optional<ServiceInfo> findServiceByOperation(String operation) {
        log.debug("Looking for service to handle operation: {}", operation);
        
        try {
            String url = hostServerUrl + "/api/registry/services/by-operation/" + operation;
            log.debug("Querying service registry at: {}", url);
            
            ServiceInfo serviceInfo = restTemplate.getForObject(url, ServiceInfo.class);
            if (serviceInfo != null) {
                log.info("Found service {} for operation: {}", serviceInfo.getName(), operation);
                return Optional.of(serviceInfo);
            }
        } catch (Exception e) {
            log.error("Failed to find service for operation: {}", operation, e);
        }
        
        log.warn("No service found for operation: {}", operation);
        return Optional.empty();
    }

    /**
     * Get detailed service information including endpoint URL
     */
    public Optional<ServiceDetails> getServiceDetails(String serviceName) {
        log.debug("Getting details for service: {}", serviceName);
        
        try {
            String url = hostServerUrl + "/api/registry/services/" + serviceName + "/details";
            log.debug("Querying service details at: {}", url);
            
            ServiceDetails details = restTemplate.getForObject(url, ServiceDetails.class);
            if (details != null) {
                log.debug("Retrieved details for service: {} with endpoint: {}", serviceName, details.getEndpoint());
                return Optional.of(details);
            }
        } catch (Exception e) {
            log.error("Failed to get details for service: {}", serviceName, e);
        }
        
        log.warn("No details found for service: {}", serviceName);
        return Optional.empty();
    }

    public static class ServiceInfo {
        private Long id;
        private String name;
        private String description;
        private String status;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class ServiceDetails {
        private String serviceName;
        private String endpoint;
        private String healthCheck;
        private String framework;
        private String status;
        private String operations;

        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
        
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        
        public String getHealthCheck() { return healthCheck; }
        public void setHealthCheck(String healthCheck) { this.healthCheck = healthCheck; }
        
        public String getFramework() { return framework; }
        public void setFramework(String framework) { this.framework = framework; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getOperations() { return operations; }
        public void setOperations(String operations) { this.operations = operations; }
    }
}