package com.angrysurfer.atomic.hostserver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_configurations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceConfiguration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    
    @Column(nullable = false)
    private String configKey;
    
    @Column(length = 4000)
    private String configValue;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConfigEnvironment environment;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConfigType type = ConfigType.STRING;
    
    @Column
    private Boolean isSecret = false;
    
    @Column(length = 1000)
    private String description;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ConfigEnvironment {
        ALL, DEVELOPMENT, STAGING, PRODUCTION, TEST
    }
    
    public enum ConfigType {
        STRING, NUMBER, BOOLEAN, JSON, URL, DATABASE_URL, API_KEY
    }
}
