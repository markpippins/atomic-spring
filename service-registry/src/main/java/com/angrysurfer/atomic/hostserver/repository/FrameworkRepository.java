package com.angrysurfer.atomic.hostserver.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.atomic.hostserver.entity.Framework;

@Repository
public interface FrameworkRepository extends JpaRepository<Framework, Long> {
    // Note: Not cached because this is used for existence checks before inserts
    Optional<Framework> findByName(String name);

    @Cacheable(value = "frameworksByCategory", key = "#categoryId")
    List<Framework> findByCategoryId(Long categoryId);

    @Cacheable(value = "frameworksByLanguage", key = "#languageId")
    List<Framework> findByLanguageId(Long languageId);
}
