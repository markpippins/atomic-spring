package com.angrysurfer.atomic.hostserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.angrysurfer.atomic.hostserver.entity.ServerType;
import java.util.Optional;

public interface ServerTypeRepository extends JpaRepository<ServerType, Long> {
    Optional<ServerType> findByName(String name);
}
