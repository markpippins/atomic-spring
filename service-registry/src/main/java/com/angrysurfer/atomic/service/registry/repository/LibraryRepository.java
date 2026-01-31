package com.angrysurfer.atomic.service.registry.repository;

import com.angrysurfer.atomic.service.registry.entity.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryRepository extends JpaRepository<Library, Long> {
    Optional<Library> findByName(String name);

    Optional<Library> findByPackageName(String packageName);

    List<Library> findByCategoryId(Long categoryId);

    List<Library> findByLanguageId(Long languageId);

    List<Library> findByPackageManager(String packageManager);
}
