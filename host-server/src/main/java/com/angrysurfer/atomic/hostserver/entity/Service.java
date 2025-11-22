package com.angrysurfer.atomic.hostserver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Service {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "framework_id")
    private Framework framework;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceType type = ServiceType.REST_API;
    
    @Column
    private String repositoryUrl;
    
    @Column
    private String version;
    
    @Column
    private Integer defaultPort;
    
    @Column
    private String healthCheckPath;
    
    @Column
    private String apiBasePath;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceStatus status = ServiceStatus.ACTIVE;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Deployment> deployments = new HashSet<>();
    
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ServiceConfiguration> configurations = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "service_dependencies",
        joinColumns = @JoinColumn(name = "service_id"),
        inverseJoinColumns = @JoinColumn(name = "depends_on_id")
    )
    private Set<Service> dependencies = new HashSet<>();
    
    @ManyToMany(mappedBy = "dependencies")
    private Set<Service> dependents = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ServiceType {
        REST_API, GRAPHQL_API, GRPC_SERVICE, MESSAGE_QUEUE, 
        DATABASE, CACHE, GATEWAY, PROXY, WEB_APP, BACKGROUND_JOB
    }
    
    public enum ServiceStatus {
        ACTIVE, DEPRECATED, ARCHIVED, PLANNED
    }
}