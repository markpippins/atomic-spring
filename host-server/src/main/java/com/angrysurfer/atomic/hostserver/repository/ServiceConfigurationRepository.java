package com.angrysurfer.atomic.hostserver.repository;

import com.angrysurfer.atomic.hostserver.entity.ServiceConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceConfigurationRepository extends JpaRepository<ServiceConfiguration, Long> {
    List<ServiceConfiguration> findByServiceId(Long serviceId);
    List<ServiceConfiguration> findByServiceIdAndEnvironment(Long serviceId, ServiceConfiguration.ConfigEnvironment environment);
    Optional<ServiceConfiguration> findByServiceIdAndConfigKeyAndEnvironment(Long serviceId, String configKey, ServiceConfiguration.ConfigEnvironment environment);
}
