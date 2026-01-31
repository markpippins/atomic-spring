package com.angrysurfer.atomic.service.registry.repository;

import com.angrysurfer.atomic.service.registry.entity.FrameworkVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FrameworkVendorRepository extends JpaRepository<FrameworkVendor, Long> {
    Optional<FrameworkVendor> findByName(String name);
}
