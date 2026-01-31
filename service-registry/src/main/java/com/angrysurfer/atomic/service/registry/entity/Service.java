package com.angrysurfer.atomic.service.registry.entity;

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
import jakarta.persistence.Transient;

@Entity
@Table(name = "services")
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "component_override_id", referencedColumnName = "id", nullable = true)
    private VisualComponent componentOverride;

    // Temporary field to maintain backward compatibility
    @Transient
    private Long componentOverrideId;

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

    public Service() {
    }

    public Service(Long id, String name, String description, Long frameworkId, Framework framework, Long serviceTypeId,
            ServiceType type, Long componentOverrideId, VisualComponent componentOverride, Long parentServiceId,
            Service parentService, Set<Service> subModules, Integer defaultPort, String apiBasePath,
            String repositoryUrl, String version, String status, Boolean activeFlag, LocalDateTime createdAt,
            LocalDateTime updatedAt, Set<Deployment> deployments, Set<ServiceConfiguration> serviceConfigs,
            Set<ServiceDependency> serviceDependenciesAsConsumer,
            Set<ServiceDependency> serviceDependenciesAsProvider) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.frameworkId = frameworkId;
        this.framework = framework;
        this.serviceTypeId = serviceTypeId;
        this.type = type;
        if (componentOverrideId != null) {
            if (componentOverride == null) {
                this.componentOverride = new VisualComponent();
                this.componentOverride.setId(componentOverrideId);
            } else {
                this.componentOverride = componentOverride;
            }
        } else {
            this.componentOverride = componentOverride;
        }
        this.parentServiceId = parentServiceId;
        this.parentService = parentService;
        this.subModules = subModules;
        this.defaultPort = defaultPort;
        this.apiBasePath = apiBasePath;
        this.repositoryUrl = repositoryUrl;
        this.version = version;
        this.status = status;
        this.activeFlag = activeFlag;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deployments = deployments;
        this.serviceConfigs = serviceConfigs;
        this.serviceDependenciesAsConsumer = serviceDependenciesAsConsumer;
        this.serviceDependenciesAsProvider = serviceDependenciesAsProvider;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getFrameworkId() {
        return frameworkId;
    }

    public void setFrameworkId(Long frameworkId) {
        this.frameworkId = frameworkId;
    }

    public Framework getFramework() {
        return framework;
    }

    public void setFramework(Framework framework) {
        this.framework = framework;
    }

    public Long getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Long serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public ServiceType getType() {
        return type;
    }

    public void setType(ServiceType type) {
        this.type = type;
    }

    public Long getComponentOverrideId() {
        return componentOverride != null ? componentOverride.getId() : null;
    }

    public void setComponentOverrideId(Long componentOverrideId) {
        if (componentOverrideId != null) {
            if (this.componentOverride == null) {
                this.componentOverride = new VisualComponent();
            }
            this.componentOverride.setId(componentOverrideId);
        } else {
            this.componentOverride = null;
        }
    }

    public VisualComponent getComponentOverride() {
        return componentOverride;
    }

    public void setComponentOverride(VisualComponent componentOverride) {
        this.componentOverride = componentOverride;
    }

    public Long getParentServiceId() {
        return parentServiceId;
    }

    public void setParentServiceId(Long parentServiceId) {
        this.parentServiceId = parentServiceId;
    }

    public Service getParentService() {
        return parentService;
    }

    public void setParentService(Service parentService) {
        this.parentService = parentService;
    }

    public Set<Service> getSubModules() {
        return subModules;
    }

    public void setSubModules(Set<Service> subModules) {
        this.subModules = subModules;
    }

    public Integer getDefaultPort() {
        return defaultPort;
    }

    public void setDefaultPort(Integer defaultPort) {
        this.defaultPort = defaultPort;
    }

    public String getApiBasePath() {
        return apiBasePath;
    }

    public void setApiBasePath(String apiBasePath) {
        this.apiBasePath = apiBasePath;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Deployment> getDeployments() {
        return deployments;
    }

    public void setDeployments(Set<Deployment> deployments) {
        this.deployments = deployments;
    }

    public Set<ServiceConfiguration> getServiceConfigs() {
        return serviceConfigs;
    }

    public void setServiceConfigs(Set<ServiceConfiguration> serviceConfigs) {
        this.serviceConfigs = serviceConfigs;
    }

    public Set<ServiceDependency> getServiceDependenciesAsConsumer() {
        return serviceDependenciesAsConsumer;
    }

    public void setServiceDependenciesAsConsumer(Set<ServiceDependency> serviceDependenciesAsConsumer) {
        this.serviceDependenciesAsConsumer = serviceDependenciesAsConsumer;
    }

    public Set<ServiceDependency> getServiceDependenciesAsProvider() {
        return serviceDependenciesAsProvider;
    }

    public void setServiceDependenciesAsProvider(Set<ServiceDependency> serviceDependenciesAsProvider) {
        this.serviceDependenciesAsProvider = serviceDependenciesAsProvider;
    }

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

    public ServiceStatus getStatusEnum() {
        return ServiceStatus.valueOf(this.status);
    }

    public enum ServiceStatus {
        ACTIVE, DEPRECATED, ARCHIVED, PLANNED
    }
}