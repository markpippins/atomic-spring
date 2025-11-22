package com.angrysurfer.atomic.hostserver.repository;

import com.angrysurfer.atomic.hostserver.entity.Framework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FrameworkRepository extends JpaRepository<Framework, Long> {
    Optional<Framework> findByName(String name);
    List<Framework> findByCategory(Framework.FrameworkCategory category);
    List<Framework> findByLanguage(String language);
    List<Framework> findBySupportsBrokerPattern(Boolean supportsBrokerPattern);
}
