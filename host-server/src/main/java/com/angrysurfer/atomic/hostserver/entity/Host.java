package com.angrysurfer.atomic.hostserver.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "servers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"deployments"})
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
        if (this == o) return true;
        if (!(o instanceof Host)) return false;
        Host host = (Host) o;
        return Objects.equals(id, host.id) &&
               Objects.equals(hostname, host.hostname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, hostname);
    }

    // Methods needed for backward compatibility with controllers and services
    public ServerType getType() {
        // This field was removed, returning null for now
        return null;
    }

    public void setType(ServerType type) {
        // This field was removed, doing nothing for now
    }

    public String getEnvironment() {
        // This field was removed, returning null for now
        return null;
    }

    public void setEnvironment(String environment) {
        // This field was removed, doing nothing for now
    }

    public String getOperatingSystem() {
        // This field was removed, returning null for now
        return null;
    }

    public void setOperatingSystem(String operatingSystem) {
        // This field was removed, doing nothing for now
    }

    public Long getMemoryMb() {
        // This field was removed, returning null for now
        return null;
    }

    public void setMemoryMb(Long memoryMb) {
        // This field was removed, doing nothing for now
    }

    public Long getDiskGb() {
        // This field was removed, returning null for now
        return null;
    }

    public void setDiskGb(Long diskGb) {
        // This field was removed, doing nothing for now
    }

    public String getRegion() {
        // This field was removed, returning null for now
        return null;
    }

    public void setRegion(String region) {
        // This field was removed, doing nothing for now
    }

    public String getCloudProvider() {
        // This field was removed, returning null for now
        return null;
    }

    public void setCloudProvider(String cloudProvider) {
        // This field was removed, doing nothing for now
    }

    public String getStatus() {
        // This field was removed, returning null for now
        return null;
    }

    public void setStatus(String status) {
        // This field was removed, doing nothing for now
    }

    public enum ServerEnvironment {
        DEVELOPMENT, STAGING, PRODUCTION, TEST
    }

    public enum ServerStatus {
        ACTIVE, INACTIVE, MAINTENANCE, DECOMMISSIONED
    }
}