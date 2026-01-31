package com.angrysurfer.atomic.hostserver.repository;

import com.angrysurfer.atomic.hostserver.entity.FrameworkVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FrameworkVendorRepository extends JpaRepository<FrameworkVendor, Long> {
    Optional<FrameworkVendor> findByName(String name);
}
