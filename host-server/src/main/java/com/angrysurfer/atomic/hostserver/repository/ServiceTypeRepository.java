package com.angrysurfer.atomic.hostserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.angrysurfer.atomic.hostserver.entity.ServiceType;
import java.util.Optional;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {
    Optional<ServiceType> findByName(String name);
}
