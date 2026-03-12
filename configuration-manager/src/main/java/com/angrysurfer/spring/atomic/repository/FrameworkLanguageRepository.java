package com.angrysurfer.spring.atomic.repository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.angrysurfer.spring.atomic.entity.FrameworkLanguage;
import java.util.Optional;

public interface FrameworkLanguageRepository extends JpaRepository<FrameworkLanguage, Long> {
    @Cacheable(value = "frameworkLanguages", key = "#name")
    Optional<FrameworkLanguage> findByName(String name);
}
