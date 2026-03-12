package com.angrysurfer.spring.atomic.repository;

import com.angrysurfer.spring.atomic.entity.Service;
import com.angrysurfer.spring.atomic.entity.ServiceConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceConfigurationRepository extends JpaRepository<ServiceConfiguration, Long> {
    List<ServiceConfiguration> findByServiceId(Long serviceId);
    List<ServiceConfiguration> findByServiceIdAndEnvironmentId(Long serviceId, Long environmentId);
    Optional<ServiceConfiguration> findByServiceIdAndConfigKeyAndEnvironmentId(Long serviceId, String configKey, Long environmentId);
    List<ServiceConfiguration> findByConfigKey(String configKey);

    // Method needed for backward compatibility with services
    Optional<ServiceConfiguration> findByServiceAndConfigKey(Service service, String configKey);
}
