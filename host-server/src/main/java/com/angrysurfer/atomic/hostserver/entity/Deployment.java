package com.angrysurfer.atomic.hostserver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "deployments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deployment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    
    @ManyToOne
    @JoinColumn(name = "server_id", nullable = false)
    private Host server;
    
    @Column(nullable = false)
    private Integer port;
    
    @Column
    private String contextPath;
    
    @Column
    private String version;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeploymentStatus status = DeploymentStatus.STOPPED;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeploymentEnvironment environment;
    
    @Column
    private String healthCheckUrl;
    
    @Column
    private LocalDateTime lastHealthCheck;
    
    @Enumerated(EnumType.STRING)
    @Column
    private HealthStatus healthStatus;
    
    @Column
    private String processId;
    
    @Column
    private String containerName;
    
    @Column
    private String deploymentPath;
    
    @Column
    private LocalDateTime deployedAt;
    
    @Column
    private LocalDateTime startedAt;
    
    @Column
    private LocalDateTime stoppedAt;
    
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
    
    public enum DeploymentStatus {
        RUNNING, STOPPED, STARTING, STOPPING, FAILED, UNKNOWN
    }
    
    public enum DeploymentEnvironment {
        DEVELOPMENT, STAGING, PRODUCTION, TEST
    }
    
    public enum HealthStatus {
        HEALTHY, UNHEALTHY, DEGRADED, UNKNOWN
    }
}
