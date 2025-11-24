package com.angrysurfer.atomic.hostserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.angrysurfer.atomic.hostserver.entity.FrameworkLanguage;
import java.util.Optional;

public interface FrameworkLanguageRepository extends JpaRepository<FrameworkLanguage, Long> {
    Optional<FrameworkLanguage> findByName(String name);
}
