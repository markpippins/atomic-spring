package com.angrysurfer.atomic.hostserver.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.angrysurfer.atomic.hostserver.entity.Deployment;
import com.angrysurfer.atomic.hostserver.entity.ServiceBackend;

@Repository
public interface ServiceBackendRepository extends JpaRepository<ServiceBackend, Long> {
    
    /**
     * Find all backends for a specific service deployment
     * Example: Get all backends for file-service instance on port 8084
     */
    List<ServiceBackend> findByServiceDeployment(Deployment serviceDeployment);
    
    /**
     * Find all backends for a service deployment by ID
     */
    List<ServiceBackend> findByServiceDeploymentId(Long serviceDeploymentId);
    
    /**
     * Find all service deployments that use a specific backend
     * Example: Find all services using file-system-server on port 4040
     */
    List<ServiceBackend> findByBackendDeployment(Deployment backendDeployment);
    
    /**
     * Find all service deployments that use a backend by ID
     */
    List<ServiceBackend> findByBackendDeploymentId(Long backendDeploymentId);
    
    /**
     * Find backends by role
     * Example: Find all PRIMARY backends for a service
     */
    List<ServiceBackend> findByServiceDeploymentAndRole(Deployment serviceDeployment, ServiceBackend.BackendRole role);
    
    /**
     * Find active backends for a service deployment, ordered by priority
     */
    @Query("SELECT sb FROM ServiceBackend sb WHERE sb.serviceDeployment = :deployment AND sb.isActive = true ORDER BY sb.priority ASC")
    List<ServiceBackend> findActiveBackendsByPriority(@Param("deployment") Deployment deployment);
    
    /**
     * Find backends by routing key (for sharding)
     */
    List<ServiceBackend> findByServiceDeploymentAndRoutingKey(Deployment serviceDeployment, String routingKey);
}
