package com.angrysurfer.atomic.hostserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.angrysurfer.atomic.hostserver.entity.FrameworkCategory;
import java.util.Optional;

public interface FrameworkCategoryRepository extends JpaRepository<FrameworkCategory, Long> {
    Optional<FrameworkCategory> findByName(String name);
}
