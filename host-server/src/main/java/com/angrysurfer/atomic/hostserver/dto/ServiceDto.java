package com.angrysurfer.atomic.hostserver.dto;

import com.angrysurfer.atomic.hostserver.entity.Service;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ServiceDto {
    private Long id;
    private String name;
    private String description;
    private FrameworkDto framework;
    private String type;
    private String repositoryUrl;
    private String version;
    private Integer defaultPort;
    private String healthCheckPath;
    private String apiBasePath;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ServiceDto fromEntity(Service service) {
        ServiceDto dto = new ServiceDto();
        dto.setId(service.getId());
        dto.setName(service.getName());
        dto.setDescription(service.getDescription());
        dto.setType(service.getServiceTypeId().toString());
        dto.setRepositoryUrl(service.getRepositoryUrl());
        dto.setVersion(service.getVersion());
        dto.setDefaultPort(service.getDefaultPort());
        dto.setHealthCheckPath(service.getHealthCheckPath());
        dto.setApiBasePath(service.getApiBasePath());
        dto.setStatus(service.getStatus()); // Updated to use string instead of enum
        dto.setCreatedAt(service.getCreatedAt());
        dto.setUpdatedAt(service.getUpdatedAt());

        // Framework is now referenced by ID instead of object
        // For now, we'll leave framework as null since we don't have the full framework object
        // In a real implementation, you'd fetch the framework by ID and populate it

        return dto;
    }
}