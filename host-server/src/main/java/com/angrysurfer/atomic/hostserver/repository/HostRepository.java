package com.angrysurfer.atomic.hostserver.repository;

import com.angrysurfer.atomic.hostserver.entity.Host;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HostRepository extends JpaRepository<Host, Long> {
    Optional<Host> findByHostname(String hostname);
    List<Host> findByEnvironment(Host.ServerEnvironment environment);
    List<Host> findByStatus(Host.ServerStatus status);
    List<Host> findByType(Host.ServerType type);
    List<Host> findByCloudProvider(String cloudProvider);
}