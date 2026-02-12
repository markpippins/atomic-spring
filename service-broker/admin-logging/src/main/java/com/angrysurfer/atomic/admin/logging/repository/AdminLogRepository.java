package com.angrysurfer.atomic.admin.logging.repository;

import com.angrysurfer.atomic.admin.logging.AdminLogEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AdminLogRepository extends MongoRepository<AdminLogEntry, UUID> {
    // Additional custom queries can be added here if needed
}