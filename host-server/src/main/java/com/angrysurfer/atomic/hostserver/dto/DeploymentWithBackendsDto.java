package com.angrysurfer.atomic.hostserver.dto;

import java.util.List;

import lombok.Data;

/**
 * DTO representing a deployment with its backend connections
 * Used for detailed deployment view in admin UI
 */
@Data
public class DeploymentWithBackendsDto {
    private Long id;
    private String serviceName;
    private String serverHostname;
    private Integer port;
    private String version;
    private String status;
    private String environment;
    
    // Backend connections
    private List<ServiceBackendDto> backends;
    
    // Services that use this deployment as a backend
    private List<ServiceBackendDto> consumers;
}
