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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Join entity linking Services to Libraries with version information.
 * Represents compile-time dependencies for a service.
 * Version is tracked independently of the library's current version.
 */
@Entity
@Table(name = "service_libraries", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"service_id", "library_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceLibrary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Service service;

    @Column(name = "library_id", nullable = false)
    private Long libraryId;

    @ManyToOne
    @JoinColumn(name = "library_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Library library;

    /**
     * The specific version of the library used by this service.
     * This is independent of the library's currentVersion field.
     */
    @Column(name = "version", nullable = false)
    private String version;

    /**
     * Optional: Version constraint/range (e.g., "^1.0.0", ">=2.0.0 <3.0.0")
     */
    @Column(name = "version_constraint")
    private String versionConstraint;

    /**
     * Dependency scope: COMPILE, RUNTIME, TEST, PROVIDED, OPTIONAL
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "scope")
    private DependencyScope scope = DependencyScope.COMPILE;

    /**
     * Whether this is a direct dependency or transitive
     */
    @Column(name = "is_direct")
    private Boolean isDirect = true;

    /**
     * Whether this is a dev/development dependency
     */
    @Column(name = "is_dev_dependency")
    private Boolean isDevDependency = false;

    @Column(length = 500)
    private String notes;

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
        if (this == o) return true;
        if (!(o instanceof ServiceLibrary)) return false;
        ServiceLibrary that = (ServiceLibrary) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Dependency scope enum
     */
    public enum DependencyScope {
        COMPILE,    // Required at compile time (default)
        RUNTIME,    // Only needed at runtime
        TEST,       // Only needed for testing
        PROVIDED,   // Provided by the container/runtime
        OPTIONAL    // Optional dependency
    }
}
