package com.angrysurfer.atomic.hostserver.repository;

import com.angrysurfer.atomic.hostserver.entity.Host;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.angrysurfer.atomic.hostserver.entity.ServerType;

@Repository
public interface HostRepository extends JpaRepository<Host, Long> {
    Optional<Host> findByHostname(String hostname);
    List<Host> findByEnvironmentTypeId(Long environmentTypeId);
    List<Host> findByStatus(String status);
    List<Host> findByServerTypeId(Long serverTypeId);
    List<Host> findByCloudProvider(String cloudProvider);
}