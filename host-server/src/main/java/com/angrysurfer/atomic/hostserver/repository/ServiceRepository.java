package com.angrysurfer.atomic.hostserver.repository;

import com.angrysurfer.atomic.hostserver.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.angrysurfer.atomic.hostserver.entity.ServiceType;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    Optional<Service> findByName(String name);
    List<Service> findByFrameworkId(Long frameworkId);
    List<Service> findByType(ServiceType type);
    List<Service> findByStatus(Service.ServiceStatus status);
    
    @Query("SELECT s FROM Service s JOIN s.dependencies d WHERE d.id = :serviceId")
    List<Service> findDependents(Long serviceId);
}