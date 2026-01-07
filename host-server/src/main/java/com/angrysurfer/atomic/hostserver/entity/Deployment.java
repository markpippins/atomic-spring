package com.angrysurfer.atomic.hostserver.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "deployments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "service.deployments", "service.configurations", "service.dependents", "server.deployments" })
public class Deployment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Service service;

    @Column(name = "environment_id", nullable = false)
    private Long environmentId;

    @Column(name = "server_id", nullable = false)
    private Long serverId;

    @ManyToOne
    @JoinColumn(name = "server_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Host server;

    @Column
    private String version;

    @Column(name = "deployed_at")
    private LocalDateTime deployedAt;

    @Column
    private String status;

    @Column
    private Integer port;

    @Column(name = "context_path")
    private String contextPath;

    @Column(name = "health_check_url")
    private String healthCheckUrl;

    @Column(name = "health_status")
    private String healthStatus;

    @Column(name = "last_health_check")
    private LocalDateTime lastHealthCheck;

    @Column(name = "process_id")
    private String processId;

    @Column(name = "container_name")
    private String containerName;

    @Column(name = "deployment_path")
    private String deploymentPath;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "stopped_at")
    private LocalDateTime stoppedAt;

    @Column(name = "active_flag")
    private Boolean activeFlag = true;

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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Deployment))
            return false;
        Deployment that = (Deployment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Helper method to get environment as string for JSON serialization
    @com.fasterxml.jackson.annotation.JsonProperty("environment")
    public String getEnvironment() {
        return getEnvironmentEnum().name();
    }

    // Helper method to get environment as enum
    public DeploymentEnvironment getEnvironmentEnum() {
        if (environmentId == null)
            return DeploymentEnvironment.DEVELOPMENT;
        // Map environment IDs to enum values
        // This is a simplified mapping - in production you'd query the environment type
        switch (environmentId.intValue()) {
            case 1:
                return DeploymentEnvironment.DEVELOPMENT;
            case 2:
                return DeploymentEnvironment.STAGING;
            case 3:
                return DeploymentEnvironment.PRODUCTION;
            case 4:
                return DeploymentEnvironment.TEST;
            default:
                return DeploymentEnvironment.DEVELOPMENT;
        }
    }

    // Helper method to get status as enum
    public DeploymentStatus getStatusEnum() {
        if (status == null)
            return DeploymentStatus.UNKNOWN;
        try {
            return DeploymentStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return DeploymentStatus.UNKNOWN;
        }
    }

    // Helper method to get health status as enum
    public HealthStatus getHealthStatusEnum() {
        if (healthStatus == null)
            return HealthStatus.UNKNOWN;
        try {
            return HealthStatus.valueOf(healthStatus);
        } catch (IllegalArgumentException e) {
            return HealthStatus.UNKNOWN;
        }
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
