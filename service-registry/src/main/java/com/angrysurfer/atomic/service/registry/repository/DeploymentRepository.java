package com.angrysurfer.atomic.service.registry.repository;

import com.angrysurfer.atomic.service.registry.entity.Deployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeploymentRepository extends JpaRepository<Deployment, Long> {
    List<Deployment> findByServiceId(Long serviceId);
    List<Deployment> findByServerId(Long serverId);
    List<Deployment> findByStatus(String status);
    List<Deployment> findByEnvironmentId(Long environmentId);
    List<Deployment> findByServiceIdAndEnvironmentId(Long serviceId, Long environmentId);
}
