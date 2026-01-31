package com.angrysurfer.atomic.hostserver.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "servers")
@JsonIgnoreProperties({ "deployments" })
public class Host {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String hostname;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "server_type_id", nullable = false)
    private Long serverTypeId;

    @ManyToOne
    @JoinColumn(name = "server_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    private ServerType type;

    @Column(name = "environment_type_id", nullable = false)
    private Long environmentTypeId;

    @ManyToOne
    @JoinColumn(name = "environment_type_id", referencedColumnName = "id", insertable = false, updatable = false)
    private EnvironmentType environmentType;

    @Column(name = "operating_system_id", nullable = false)
    private Long operatingSystemId;

    @ManyToOne
    @JoinColumn(name = "operating_system_id", referencedColumnName = "id", insertable = false, updatable = false)
    private OperatingSystem operatingSystem;

    @Column(name = "cpu_cores")
    private Integer cpuCores;

    @Column
    private String memory;

    @Column
    private String disk;

    @Column
    private String status;

    @Column
    private String region;

    @Column(name = "cloud_provider")
    private String cloudProvider;

    @Column(length = 1000)
    private String description;

    @Column(name = "active_flag")
    private Boolean activeFlag = true;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Deployment> deployments = new HashSet<>();

    public Host() {
    }

    public Host(Long id, String hostname, String ipAddress, Long serverTypeId, ServerType type, Long environmentTypeId,
            EnvironmentType environmentType, Long operatingSystemId, OperatingSystem operatingSystem, Integer cpuCores,
            String memory, String disk, String status, String region, String cloudProvider, String description,
            Boolean activeFlag, LocalDateTime createdAt, LocalDateTime updatedAt, Set<Deployment> deployments) {
        this.id = id;
        this.hostname = hostname;
        this.ipAddress = ipAddress;
        this.serverTypeId = serverTypeId;
        this.type = type;
        this.environmentTypeId = environmentTypeId;
        this.environmentType = environmentType;
        this.operatingSystemId = operatingSystemId;
        this.operatingSystem = operatingSystem;
        this.cpuCores = cpuCores;
        this.memory = memory;
        this.disk = disk;
        this.status = status;
        this.region = region;
        this.cloudProvider = cloudProvider;
        this.description = description;
        this.activeFlag = activeFlag;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deployments = deployments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Long getServerTypeId() {
        return serverTypeId;
    }

    public void setServerTypeId(Long serverTypeId) {
        this.serverTypeId = serverTypeId;
    }

    public ServerType getType() {
        return type;
    }

    public void setType(ServerType type) {
        this.type = type;
    }

    public Long getEnvironmentTypeId() {
        return environmentTypeId;
    }

    public void setEnvironmentTypeId(Long environmentTypeId) {
        this.environmentTypeId = environmentTypeId;
    }

    public EnvironmentType getEnvironmentType() {
        return environmentType;
    }

    public void setEnvironmentType(EnvironmentType environmentType) {
        this.environmentType = environmentType;
    }

    public Long getOperatingSystemId() {
        return operatingSystemId;
    }

    public void setOperatingSystemId(Long operatingSystemId) {
        this.operatingSystemId = operatingSystemId;
    }

    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(OperatingSystem operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public Integer getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(Integer cpuCores) {
        this.cpuCores = cpuCores;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getDisk() {
        return disk;
    }

    public void setDisk(String disk) {
        this.disk = disk;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCloudProvider() {
        return cloudProvider;
    }

    public void setCloudProvider(String cloudProvider) {
        this.cloudProvider = cloudProvider;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        if (!(o instanceof Host))
            return false;
        Host host = (Host) o;
        return Objects.equals(id, host.id) &&
                Objects.equals(hostname, host.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hostname);
    }
}