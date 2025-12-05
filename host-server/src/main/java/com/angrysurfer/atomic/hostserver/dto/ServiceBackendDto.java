package com.angrysurfer.atomic.hostserver.dto;

import com.angrysurfer.atomic.hostserver.entity.ServiceBackend;

import lombok.Data;

/**
 * DTO for ServiceBackend entity
 * Used for API requests/responses
 */
@Data
public class ServiceBackendDto {
    private Long id;
    private Long serviceDeploymentId;
    private Long backendDeploymentId;
    private ServiceBackend.BackendRole role;
    private Integer priority;
    private String routingKey;
    private Integer weight;
    private Boolean isActive;
    private String description;
    
    // Enriched data for frontend
    private String serviceDeploymentName;  // "file-service (localhost:8084)"
    private String backendDeploymentName;  // "file-system-server (localhost:4040)"
    private String backendStatus;          // "HEALTHY", "UNHEALTHY", "UNKNOWN"
}
