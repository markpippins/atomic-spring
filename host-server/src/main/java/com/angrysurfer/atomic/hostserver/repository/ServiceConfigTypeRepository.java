package com.angrysurfer.atomic.hostserver.repository;

import com.angrysurfer.atomic.hostserver.entity.ServiceConfigType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceConfigTypeRepository extends JpaRepository<ServiceConfigType, Long> {
    Optional<ServiceConfigType> findByName(String name);
}
