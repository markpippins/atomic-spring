package com.angrysurfer.spring.atomic.repository;

import com.angrysurfer.spring.atomic.entity.FrameworkVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FrameworkVendorRepository extends JpaRepository<FrameworkVendor, Long> {
    Optional<FrameworkVendor> findByName(String name);
}
