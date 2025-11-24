package com.angrysurfer.atomic.hostserver.dto;

import com.angrysurfer.atomic.hostserver.entity.Host;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ServerDto {
    private Long id;
    private String hostname;
    private String ipAddress;
    private String type;
    private String environment;
    private String operatingSystem;
    private Integer cpuCores;
    private Long memoryMb;
    private Long diskGb;
    private String region;
    private String cloudProvider;
    private String status;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ServerDto fromEntity(Host host) {
        ServerDto dto = new ServerDto();
        dto.setId(host.getId());
        dto.setHostname(host.getHostname());
        dto.setIpAddress(host.getIpAddress());
        dto.setType(host.getType() != null ? host.getType().getName() : null);
        dto.setEnvironment(host.getEnvironment() != null ? host.getEnvironment().name() : null);
        dto.setOperatingSystem(host.getOperatingSystem());
        dto.setCpuCores(host.getCpuCores());
        dto.setMemoryMb(host.getMemoryMb());
        dto.setDiskGb(host.getDiskGb());
        dto.setRegion(host.getRegion());
        dto.setCloudProvider(host.getCloudProvider());
        dto.setStatus(host.getStatus() != null ? host.getStatus().name() : null);
        dto.setDescription(host.getDescription());
        dto.setCreatedAt(host.getCreatedAt());
        dto.setUpdatedAt(host.getUpdatedAt());
        return dto;
    }
}