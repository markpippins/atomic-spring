package com.angrysurfer.atomic.hostserver.repository;

import com.angrysurfer.atomic.hostserver.entity.Deployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeploymentRepository extends JpaRepository<Deployment, Long> {
    List<Deployment> findByServiceId(Long serviceId);
    List<Deployment> findByServerId(Long serverId);
    List<Deployment> findByStatus(Deployment.DeploymentStatus status);
    List<Deployment> findByEnvironment(Deployment.DeploymentEnvironment environment);
    List<Deployment> findByServiceIdAndEnvironment(Long serviceId, Deployment.DeploymentEnvironment environment);
}
