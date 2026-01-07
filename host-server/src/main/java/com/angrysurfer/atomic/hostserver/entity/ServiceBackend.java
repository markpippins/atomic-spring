package com.angrysurfer.atomic.hostserver.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

/**
 * Represents a backend connection between service instances.
 * 
 * Example: A file-service instance (deployment) connects to one or more 
 * file-system-server instances (backend deployments) for storage.
 * 
 * Supports multiple backends per service for:
 * - Primary/backup configurations
 * - Load balancing
 * - Data sharding
 * - Caching layers
 */
@Entity
@Table(name = "service_backends")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBackend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The service instance that uses this backend
     * Example: file-service deployment on port 8084
     */
    @Column(name = "service_deployment_id", nullable = false)
    private Long serviceDeploymentId;

    @ManyToOne
    @JoinColumn(name = "service_deployment_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Deployment serviceDeployment;

    /**
     * The backend service instance being used
     * Example: file-system-server deployment on port 4040
     */
    @Column(name = "backend_deployment_id", nullable = false)
    private Long backendDeploymentId;

    @ManyToOne
    @JoinColumn(name = "backend_deployment_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Deployment backendDeployment;

    // Getter and setter methods for the new fields
    public Long getServiceDeploymentId() {
        return serviceDeploymentId;
    }

    public void setServiceDeploymentId(Long serviceDeploymentId) {
        this.serviceDeploymentId = serviceDeploymentId;
    }

    public Long getBackendDeploymentId() {
        return backendDeploymentId;
    }

    public void setBackendDeploymentId(Long backendDeploymentId) {
        this.backendDeploymentId = backendDeploymentId;
    }

    /**
     * Role of this backend in the service architecture
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BackendRole role = BackendRole.PRIMARY;

    /**
     * Priority for failover (1 = highest priority)
     * Used when multiple backends have the same role
     */
    @Column
    private Integer priority = 1;

    /**
     * Routing key for sharding/partitioning
     * Example: "users-a-m", "region-us-east", "tenant-123"
     */
    @Column(length = 100)
    private String routingKey;

    /**
     * Weight for load balancing (higher = more traffic)
     */
    @Column
    private Integer weight = 100;

    /**
     * Whether this backend is currently active
     */
    @Column(nullable = false)
    private Boolean isActive = true;

    /**
     * Description of this backend connection
     */
    @Column(length = 500)
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServiceBackend)) return false;
        ServiceBackend that = (ServiceBackend) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Role of a backend in the service architecture
     */
    public enum BackendRole {
        /**
         * Primary backend - main data source
         */
        PRIMARY,
        
        /**
         * Backup backend - used when primary fails
         */
        BACKUP,
        
        /**
         * Archive backend - cold storage for old data
         */
        ARCHIVE,
        
        /**
         * Cache backend - hot cache layer
         */
        CACHE,
        
        /**
         * Shard backend - handles a partition of data
         */
        SHARD,
        
        /**
         * Read replica - handles read-only queries
         */
        READ_REPLICA
    }
}
