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
        dto.setType(service.getType() != null ? service.getType().getName() : null);
        dto.setRepositoryUrl(service.getRepositoryUrl());
        dto.setVersion(service.getVersion());
        dto.setDefaultPort(service.getDefaultPort());
        dto.setHealthCheckPath(service.getHealthCheckPath());
        dto.setApiBasePath(service.getApiBasePath());
        dto.setStatus(service.getStatus() != null ? service.getStatus().name() : null);
        dto.setCreatedAt(service.getCreatedAt());
        dto.setUpdatedAt(service.getUpdatedAt());
        
        if (service.getFramework() != null) {
            dto.setFramework(FrameworkDto.fromEntity(service.getFramework()));
        }
        
        return dto;
    }
}