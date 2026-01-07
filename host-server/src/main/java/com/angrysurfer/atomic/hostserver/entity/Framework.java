package com.angrysurfer.atomic.hostserver.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "frameworks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "services" })
public class Framework {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "vendor_id", nullable = false)
    private Long vendorId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", insertable = false, updatable = false)
    private FrameworkCategory category;

    @Column(name = "language_id", nullable = false)
    private Long languageId;

    @ManyToOne
    @JoinColumn(name = "language_id", referencedColumnName = "id", insertable = false, updatable = false)
    private FrameworkLanguage language;

    @Column(name = "current_version")
    private String currentVersion;

    @Column(name = "lts_version")
    private String ltsVersion;

    @Column
    private String url;

    @Column(name = "active_flag")
    private Boolean activeFlag = true;

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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Framework))
            return false;
        Framework framework = (Framework) o;
        return Objects.equals(id, framework.id) &&
                Objects.equals(name, framework.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    // Methods needed for backward compatibility with controllers and services
    public String getLatestVersion() {
        // This field was removed, returning null for now
        return null;
    }

    public void setLatestVersion(String latestVersion) {
        // This field was removed, doing nothing for now
    }

    public String getDocumentationUrl() {
        // This field was removed, returning null for now
        return null;
    }

    public void setDocumentationUrl(String documentationUrl) {
        // This field was removed, doing nothing for now
    }

    public String getRepositoryUrl() {
        // This field was removed, returning null for now
        return null;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        // This field was removed, doing nothing for now
    }

    public Boolean getSupportsBrokerPattern() {
        // This field was removed, returning false for now
        return false;
    }

    public void setSupportsBrokerPattern(Boolean supportsBrokerPattern) {
        // This field was removed, doing nothing for now
    }
}
