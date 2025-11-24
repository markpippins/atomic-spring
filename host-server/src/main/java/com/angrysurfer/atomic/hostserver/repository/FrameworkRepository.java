package com.angrysurfer.atomic.hostserver.repository;

import com.angrysurfer.atomic.hostserver.entity.Framework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.angrysurfer.atomic.hostserver.entity.FrameworkCategory;
import com.angrysurfer.atomic.hostserver.entity.FrameworkLanguage;

@Repository
public interface FrameworkRepository extends JpaRepository<Framework, Long> {
    Optional<Framework> findByName(String name);
    List<Framework> findByCategory(FrameworkCategory category);
    List<Framework> findByLanguage(FrameworkLanguage language);
    List<Framework> findBySupportsBrokerPattern(Boolean supportsBrokerPattern);
}
