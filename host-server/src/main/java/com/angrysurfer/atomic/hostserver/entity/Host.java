package com.angrysurfer.atomic.hostserver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "servers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Host {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String hostname;
    
    @Column(nullable = false)
    private String ipAddress;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServerType type = ServerType.VIRTUAL;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServerEnvironment environment = ServerEnvironment.DEVELOPMENT;
    
    @Column
    private String operatingSystem;
    
    @Column
    private Integer cpuCores;
    
    @Column
    private Long memoryMb;
    
    @Column
    private Long diskGb;
    
    @Column
    private String region;
    
    @Column
    private String cloudProvider;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServerStatus status = ServerStatus.ACTIVE;
    
    @Column
    private String description;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Deployment> deployments = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ServerType {
        PHYSICAL, VIRTUAL, CONTAINER, CLOUD
    }
    
    public enum ServerEnvironment {
        DEVELOPMENT, STAGING, PRODUCTION, TEST
    }
    
    public enum ServerStatus {
        ACTIVE, INACTIVE, MAINTENANCE, DECOMMISSIONED
    }
}