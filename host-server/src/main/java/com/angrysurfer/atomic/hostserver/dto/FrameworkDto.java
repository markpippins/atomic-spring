package com.angrysurfer.atomic.hostserver.dto;

import com.angrysurfer.atomic.hostserver.entity.Framework;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FrameworkDto {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String language;
    private String latestVersion;
    private String documentationUrl;
    private String repositoryUrl;
    private Boolean supportsBrokerPattern;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FrameworkDto fromEntity(Framework framework) {
        FrameworkDto dto = new FrameworkDto();
        dto.setId(framework.getId());
        dto.setName(framework.getName());
        dto.setDescription(framework.getDescription());
        dto.setCategory(framework.getCategory() != null ? framework.getCategory().getName() : null);
        dto.setLanguage(framework.getLanguage() != null ? framework.getLanguage().getName() : null);
        dto.setLatestVersion(framework.getLatestVersion());
        dto.setDocumentationUrl(framework.getDocumentationUrl());
        dto.setRepositoryUrl(framework.getRepositoryUrl());
        dto.setSupportsBrokerPattern(framework.getSupportsBrokerPattern());
        dto.setCreatedAt(framework.getCreatedAt());
        dto.setUpdatedAt(framework.getUpdatedAt());
        return dto;
    }
}