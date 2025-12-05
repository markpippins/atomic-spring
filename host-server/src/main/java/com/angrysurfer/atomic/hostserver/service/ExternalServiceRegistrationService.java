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
            Optional<Framework> framework = frameworkRepository.findByName(registration.getFramework());
            framework.ifPresent(service::setFramework);
        }
        
        ServiceType serviceType = serviceTypeRepository.findByName("REST_API")
                .orElseGet(() -> {
                    ServiceType newType = new ServiceType();
                    newType.setName("REST_API");
                    newType.setDescription("REST API Service");
                    return serviceTypeRepository.save(newType);
                });
        service.setType(serviceType);
        
        service = serviceRepository.save(service);
        
        if (registration.getOperations() != null) {
            storeOperations(service, registration.getOperations());
        }
        
        if (registration.getMetadata() != null) {
            storeMetadata(service, registration.getMetadata());
        }
        
        log.info("Successfully registered service: {} with ID: {}", service.getName(), service.getId());
        
        return service;
    }
    
    private void storeOperations(com.angrysurfer.atomic.hostserver.entity.Service service, 
                                  List<String> operations) {
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
        
        serviceConfigurationRepository.save(config);
    }
    
    private void storeMetadata(com.angrysurfer.atomic.hostserver.entity.Service service, 
                               java.util.Map<String, Object> metadata) {
        for (java.util.Map.Entry<String, Object> entry : metadata.entrySet()) {
            ServiceConfiguration config = serviceConfigurationRepository
                    .findByServiceAndConfigKey(service, "metadata." + entry.getKey())
                    .orElse(new ServiceConfiguration());
            
            config.setService(service);
            config.setConfigKey("metadata." + entry.getKey());
            config.setConfigValue(entry.getValue().toString());
            config.setEnvironment(ServiceConfiguration.ConfigEnvironment.ALL);
            config.setType(ServiceConfiguration.ConfigType.STRING);
            config.setDescription("Metadata: " + entry.getKey());
            
            serviceConfigurationRepository.save(config);
        }
    }
    
    @Transactional
    public boolean updateHeartbeat(String serviceName) {
        Optional<com.angrysurfer.atomic.hostserver.entity.Service> serviceOpt = 
                serviceRepository.findByName(serviceName);
        
        if (serviceOpt.isPresent()) {
            com.angrysurfer.atomic.hostserver.entity.Service service = serviceOpt.get();
            
            ServiceConfiguration heartbeatConfig = serviceConfigurationRepository
                    .findByServiceAndConfigKey(service, "lastHeartbeat")
                    .orElse(new ServiceConfiguration());
            
            heartbeatConfig.setService(service);
            heartbeatConfig.setConfigKey("lastHeartbeat");
            heartbeatConfig.setConfigValue(LocalDateTime.now().toString());
            heartbeatConfig.setEnvironment(ServiceConfiguration.ConfigEnvironment.ALL);
            heartbeatConfig.setType(ServiceConfiguration.ConfigType.STRING);
            heartbeatConfig.setDescription("Last heartbeat timestamp");
            
            serviceConfigurationRepository.save(heartbeatConfig);
            
            log.debug("Updated heartbeat for service: {}", serviceName);
            return true;
        }
        
        return false;
    }
    
    public List<com.angrysurfer.atomic.hostserver.entity.Service> getAllActiveServices() {
        return serviceRepository.findByStatus(
                com.angrysurfer.atomic.hostserver.entity.Service.ServiceStatus.ACTIVE);
    }
    
    public Optional<com.angrysurfer.atomic.hostserver.entity.Service> findServiceByOperation(String operation) {
        List<ServiceConfiguration> configs = serviceConfigurationRepository.findByConfigKey("operations");
        
        for (ServiceConfiguration config : configs) {
            String operations = config.getConfigValue();
            if (operations != null && operations.contains(operation)) {
                return Optional.of(config.getService());
            }
        }
        
        return Optional.empty();
    }
    
    @Transactional
    public boolean deregisterService(String serviceName) {
        Optional<com.angrysurfer.atomic.hostserver.entity.Service> serviceOpt = 
                serviceRepository.findByName(serviceName);
        
        if (serviceOpt.isPresent()) {
            com.angrysurfer.atomic.hostserver.entity.Service service = serviceOpt.get();
            service.setStatus(com.angrysurfer.atomic.hostserver.entity.Service.ServiceStatus.ARCHIVED);
            serviceRepository.save(service);
            
            log.info("Deregistered service: {}", serviceName);
            return true;
        }
        
        return false;
    }
}