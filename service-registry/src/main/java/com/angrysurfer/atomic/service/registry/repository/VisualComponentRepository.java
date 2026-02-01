package com.angrysurfer.atomic.service.registry.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.atomic.service.registry.entity.VisualComponent;

@Repository
public interface VisualComponentRepository extends JpaRepository<VisualComponent, Long> {
    Optional<VisualComponent> findByType(String type);
}
