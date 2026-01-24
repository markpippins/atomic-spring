package com.angrysurfer.atomic.hostserver.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "deployments", "serviceConfigs", "serviceDependenciesAsConsumer",
        "serviceDependenciesAsProvider", "parentService", "subModules" })
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "framework_id", nullable = false)
    private Long frameworkId;

    @ManyToOne
    @JoinColumn(name = "framework_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Framework framework;

    @Column(name = "service_type_id", nullable = false)
    private Long serviceTypeId;

    @ManyToOne
    @JoinColumn(name = "service_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ServiceType type;

    @Column(name = "component_override_id")
    private Long componentOverrideId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "component_override_id", referencedColumnName = "id", insertable = false, updatable = false)
    private VisualComponent componentOverride;

    // Parent service ID - null if this is a standalone/parent service
    @Column(name = "parent_service_id")
    private Long parentServiceId;

    // Parent service relationship
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_service_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Service parentService;

    // Child sub-module services
    @OneToMany(mappedBy = "parentService", fetch = FetchType.LAZY)
    private Set<Service> subModules = new HashSet<>();

    @Column(name = "default_port")
    private Integer defaultPort;

    @Column(name = "api_base_path")
    private String apiBasePath;

    @Column(name = "repository_url")
    private String repositoryUrl;

    @Column
    private String version;

    @Column
    private String status;

    @Column(name = "active_flag")
    private Boolean activeFlag = true;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Deployment> deployments = new HashSet<>();

    // This would need to be mapped to the ServiceConfig entity
    @OneToMany(mappedBy = "service")
    private Set<ServiceConfiguration> serviceConfigs = new HashSet<>();

    // For service dependencies - we'll need to create a separate entity for this
    @OneToMany(mappedBy = "service")
    private Set<ServiceDependency> serviceDependenciesAsConsumer = new HashSet<>();

    @OneToMany(mappedBy = "targetService")
    private Set<ServiceDependency> serviceDependenciesAsProvider = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Service))
            return false;
        Service service = (Service) o;
        return Objects.equals(id, service.id) &&
                Objects.equals(name, service.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    // Methods needed for backward compatibility with controllers and services
    public String getHealthCheckPath() {
        // This field was removed, returning null for now
        return null;
    }

    public void setHealthCheckPath(String healthCheckPath) {
        // This field was removed, doing nothing for now
    }

    public java.util.Set<Service> getDependencies() {
        // This field was removed, returning empty set for now
        return new java.util.HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public ServiceStatus getStatusEnum() {
        return ServiceStatus.valueOf(this.status);
    }

    public enum ServiceStatus {
        ACTIVE, DEPRECATED, ARCHIVED, PLANNED
    }
}