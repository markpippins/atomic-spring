package com.angrysurfer.atomic.hostserver.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.angrysurfer.atomic.hostserver.dto.ExternalServiceRegistration;
import com.angrysurfer.atomic.hostserver.entity.Framework;
import com.angrysurfer.atomic.hostserver.entity.ServiceConfiguration;
import com.angrysurfer.atomic.hostserver.entity.ServiceType;
import com.angrysurfer.atomic.hostserver.repository.FrameworkRepository;
import com.angrysurfer.atomic.hostserver.repository.ServiceConfigurationRepository;
import com.angrysurfer.atomic.hostserver.repository.ServiceRepository;
import com.angrysurfer.atomic.hostserver.repository.ServiceTypeRepository;

@Service
public class ExternalServiceRegistrationService {
    
    private static final Logger log = LoggerFactory.getLogger(ExternalServiceRegistrationService.class);
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private FrameworkRepository frameworkRepository;
    
    @Autowired
    private ServiceTypeRepository serviceTypeRepository;
    
    @Autowired
    private ServiceConfigurationRepository serviceConfigurationRepository;
    
    @Transactional
    public com.angrysurfer.atomic.hostserver.entity.Service registerExternalService(
            ExternalServiceRegistration registration) {

        log.info("Registering external service: {}", registration.getServiceName());
        log.debug("Registration details: version={}, endpoint={}, port={}, healthCheck={}",
                 registration.getVersion(), registration.getEndpoint(), registration.getPort(), registration.getHealthCheck());

        com.angrysurfer.atomic.hostserver.entity.Service service =
                serviceRepository.findByName(registration.getServiceName())
                        .orElse(new com.angrysurfer.atomic.hostserver.entity.Service());

        service.setName(registration.getServiceName());
        service.setDescription("External service registered via API");
        service.setVersion(registration.getVersion());
        service.setHealthCheckPath(registration.getHealthCheck());
        service.setApiBasePath(registration.getEndpoint());
        service.setDefaultPort(registration.getPort());
        service.setStatus(com.angrysurfer.atomic.hostserver.entity.Service.ServiceStatus.ACTIVE);

        if (registration.getFramework() != null) {
            log.debug("Looking up framework: {}", registration.getFramework());
            Optional<Framework> framework = frameworkRepository.findByName(registration.getFramework());
            if (framework.isPresent()) {
                service.setFramework(framework.get());
                log.debug("Framework found and assigned: {}", framework.get().getName());
            } else {
                log.warn("Framework not found: {}", registration.getFramework());
            }
        }

        ServiceType serviceType = serviceTypeRepository.findByName("REST_API")
                .orElseGet(() -> {
                    log.debug("Creating default REST_API service type");
                    ServiceType newType = new ServiceType();
                    newType.setName("REST_API");
                    newType.setDescription("REST API Service");
                    return serviceTypeRepository.save(newType);
                });
        service.setType(serviceType);
        log.debug("Assigned service type: {}", serviceType.getName());

        service = serviceRepository.save(service);
        log.debug("Service saved with ID: {}", service.getId());

        if (registration.getOperations() != null && !registration.getOperations().isEmpty()) {
            log.debug("Storing {} operations for service: {}", registration.getOperations().size(), service.getName());
            storeOperations(service, registration.getOperations());
        } else {
            log.debug("No operations to store for service: {}", service.getName());
        }

        if (registration.getMetadata() != null && !registration.getMetadata().isEmpty()) {
            log.debug("Storing metadata for service: {}", service.getName());
            storeMetadata(service, registration.getMetadata());
        } else {
            log.debug("No metadata to store for service: {}", service.getName());
        }

        log.info("Successfully registered service: {} with ID: {}", service.getName(), service.getId());

        return service;
    }
    
    private void storeOperations(com.angrysurfer.atomic.hostserver.entity.Service service,
                                  List<String> operations) {
        log.debug("Storing operations for service: {} - Operations: {}", service.getName(), operations);
        String operationsStr = String.join(",", operations);

        ServiceConfiguration config = serviceConfigurationRepository
                .findByServiceAndConfigKey(service, "operations")
                .orElse(new ServiceConfiguration());

        config.setService(service);
        config.setConfigKey("operations");
        config.setConfigValue(operationsStr);
        config.setEnvironment(ServiceConfiguration.ConfigEnvironment.ALL);
        config.setType(ServiceConfiguration.ConfigType.STRING);
        config.setDescription("Supported operations");

        ServiceConfiguration savedConfig = serviceConfigurationRepository.save(config);
        log.debug("Stored operations configuration with ID: {} for service: {}", savedConfig.getId(), service.getName());
    }
    
    private void storeMetadata(com.angrysurfer.atomic.hostserver.entity.Service service,
                               java.util.Map<String, Object> metadata) {
        log.debug("Storing {} metadata entries for service: {}", metadata.size(), service.getName());
        for (java.util.Map.Entry<String, Object> entry : metadata.entrySet()) {
            log.debug("Processing metadata: {}={}", entry.getKey(), entry.getValue());
            ServiceConfiguration config = serviceConfigurationRepository
                    .findByServiceAndConfigKey(service, "metadata." + entry.getKey())
                    .orElse(new ServiceConfiguration());

            config.setService(service);
            config.setConfigKey("metadata." + entry.getKey());
            config.setConfigValue(entry.getValue().toString());
            config.setEnvironment(ServiceConfiguration.ConfigEnvironment.ALL);
            config.setType(ServiceConfiguration.ConfigType.STRING);
            config.setDescription("Metadata: " + entry.getKey());

            ServiceConfiguration savedConfig = serviceConfigurationRepository.save(config);
            log.debug("Stored metadata configuration with ID: {} for key: {}", savedConfig.getId(), entry.getKey());
        }
    }
    
    @Transactional
    public boolean updateHeartbeat(String serviceName) {
        log.debug("Updating heartbeat for service: {}", serviceName);
        Optional<com.angrysurfer.atomic.hostserver.entity.Service> serviceOpt =
                serviceRepository.findByName(serviceName);

        if (serviceOpt.isPresent()) {
            com.angrysurfer.atomic.hostserver.entity.Service service = serviceOpt.get();
            log.debug("Service found with ID: {} for heartbeat update", service.getId());

            ServiceConfiguration heartbeatConfig = serviceConfigurationRepository
                    .findByServiceAndConfigKey(service, "lastHeartbeat")
                    .orElse(new ServiceConfiguration());

            heartbeatConfig.setService(service);
            heartbeatConfig.setConfigKey("lastHeartbeat");
            heartbeatConfig.setConfigValue(LocalDateTime.now().toString());
            heartbeatConfig.setEnvironment(ServiceConfiguration.ConfigEnvironment.ALL);
            heartbeatConfig.setType(ServiceConfiguration.ConfigType.STRING);
            heartbeatConfig.setDescription("Last heartbeat timestamp");

            ServiceConfiguration savedConfig = serviceConfigurationRepository.save(heartbeatConfig);
            log.info("Updated heartbeat for service: {} with configuration ID: {}", serviceName, savedConfig.getId());
            return true;
        }

        log.warn("Service not found for heartbeat update: {}", serviceName);
        return false;
    }
    
    public List<com.angrysurfer.atomic.hostserver.entity.Service> getAllActiveServices() {
        return serviceRepository.findByStatus(
                com.angrysurfer.atomic.hostserver.entity.Service.ServiceStatus.ACTIVE);
    }
    
    public Optional<com.angrysurfer.atomic.hostserver.entity.Service> findServiceByOperation(String operation) {
        log.debug("Finding service by operation: {}", operation);
        List<ServiceConfiguration> configs = serviceConfigurationRepository.findByConfigKey("operations");
        log.debug("Found {} operation configurations to search", configs.size());

        for (ServiceConfiguration config : configs) {
            String operations = config.getConfigValue();
            if (operations != null && operations.contains(operation)) {
                log.debug("Found service {} for operation: {}", config.getService().getName(), operation);
                return Optional.of(config.getService());
            }
        }

        log.warn("No service found for operation: {}", operation);
        return Optional.empty();
    }
    
    @Transactional
    public boolean deregisterService(String serviceName) {
        log.info("Deregistering service: {}", serviceName);
        Optional<com.angrysurfer.atomic.hostserver.entity.Service> serviceOpt =
                serviceRepository.findByName(serviceName);

        if (serviceOpt.isPresent()) {
            com.angrysurfer.atomic.hostserver.entity.Service service = serviceOpt.get();
            log.debug("Service found with ID: {} for deregistration", service.getId());
            service.setStatus(com.angrysurfer.atomic.hostserver.entity.Service.ServiceStatus.ARCHIVED);
            serviceRepository.save(service);

            log.info("Successfully deregistered service: {} with ID: {}", serviceName, service.getId());
            return true;
        }

        log.warn("Service not found for deregistration: {}", serviceName);
        return false;
    }
}