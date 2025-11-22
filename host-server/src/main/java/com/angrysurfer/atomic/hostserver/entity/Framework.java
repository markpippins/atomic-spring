package com.angrysurfer.atomic.hostserver.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "frameworks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Framework {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FrameworkCategory category;
    
    @Column
    private String language;
    
    @Column
    private String latestVersion;
    
    @Column
    private String documentationUrl;
    
    @Column
    private String repositoryUrl;
    
    @Column
    private Boolean supportsBrokerPattern = false;
    
    @Column
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "framework")
    private Set<Service> services = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum FrameworkCategory {
        JAVA_SPRING, JAVA_QUARKUS, JAVA_MICRONAUT, 
        NODE_EXPRESS, NODE_NESTJS, NODE_ADONISJS, NODE_MOLECULER,
        PYTHON_DJANGO, PYTHON_FLASK, PYTHON_FASTAPI,
        DOTNET_ASPNET, GO_GIN, RUST_ACTIX, OTHER
    }
}
