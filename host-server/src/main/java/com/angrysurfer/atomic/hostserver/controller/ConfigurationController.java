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

import com.angrysurfer.atomic.hostserver.entity.ServiceConfiguration;
import com.angrysurfer.atomic.hostserver.repository.ServiceConfigurationRepository;

@RestController
@RequestMapping("/api/configurations")
@CrossOrigin(origins = "*")
public class ConfigurationController {
    
    @Autowired
    private ServiceConfigurationRepository configurationRepository;
    
    @GetMapping
    public List<ServiceConfiguration> getAllConfigurations() {
        return configurationRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ServiceConfiguration> getConfigurationById(@PathVariable Long id) {
        return configurationRepository.findById(id)
            .map(config -> ResponseEntity.ok(config))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/service/{serviceId}")
    public List<ServiceConfiguration> getConfigurationsByService(@PathVariable Long serviceId) {
        return configurationRepository.findByServiceId(serviceId);
    }
    
    @GetMapping("/service/{serviceId}/environment/{environment}")
    public List<ServiceConfiguration> getConfigurationsByServiceAndEnvironment(
            @PathVariable Long serviceId,
            @PathVariable ServiceConfiguration.ConfigEnvironment environment) {
        return configurationRepository.findByServiceIdAndEnvironment(serviceId, environment);
    }
    
    @GetMapping("/service/{serviceId}/key/{configKey}/environment/{environment}")
    public ResponseEntity<ServiceConfiguration> getConfigurationByKey(
            @PathVariable Long serviceId,
            @PathVariable String configKey,
            @PathVariable ServiceConfiguration.ConfigEnvironment environment) {
        return configurationRepository.findByServiceIdAndConfigKeyAndEnvironment(serviceId, configKey, environment)
            .map(config -> ResponseEntity.ok(config))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<ServiceConfiguration> createConfiguration(@RequestBody ServiceConfiguration configuration) {
        ServiceConfiguration savedConfiguration = configurationRepository.save(configuration);
        return new ResponseEntity<>(savedConfiguration, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ServiceConfiguration> updateConfiguration(
            @PathVariable Long id, 
            @RequestBody ServiceConfiguration configDetails) {
        return configurationRepository.findById(id)
            .map(config -> {
                config.setConfigKey(configDetails.getConfigKey());
                config.setConfigValue(configDetails.getConfigValue());
                config.setEnvironment(configDetails.getEnvironment());
                config.setType(configDetails.getType());
                config.setIsSecret(configDetails.getIsSecret());
                config.setDescription(configDetails.getDescription());
                return ResponseEntity.ok(configurationRepository.save(config));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConfiguration(@PathVariable Long id) {
        return configurationRepository.findById(id)
            .map(config -> {
                configurationRepository.delete(config);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
