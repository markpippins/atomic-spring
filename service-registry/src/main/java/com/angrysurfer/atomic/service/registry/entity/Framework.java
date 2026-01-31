package com.angrysurfer.atomic.service.registry.entity;

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

@Entity
@Table(name = "frameworks")
@JsonIgnoreProperties({ "services" })
public class Framework {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "vendor_id")
    private Long vendorId;

    @ManyToOne
    @JoinColumn(name = "vendor_id", referencedColumnName = "id", insertable = false, updatable = false)
    private FrameworkVendor vendor;

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

    public Framework() {
    }

    public Framework(Long id, String name, String description, Long vendorId, Long categoryId,
            FrameworkCategory category, Long languageId, FrameworkLanguage language, String currentVersion,
            String ltsVersion, String url, Boolean activeFlag, LocalDateTime createdAt, LocalDateTime updatedAt,
            Set<Service> services) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.vendorId = vendorId;
        this.categoryId = categoryId;
        this.category = category;
        this.languageId = languageId;
        this.language = language;
        this.currentVersion = currentVersion;
        this.ltsVersion = ltsVersion;
        this.url = url;
        this.activeFlag = activeFlag;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.services = services;
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

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public FrameworkVendor getVendor() {
        return vendor;
    }

    public void setVendor(FrameworkVendor vendor) {
        this.vendor = vendor;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public FrameworkCategory getCategory() {
        return category;
    }

    public void setCategory(FrameworkCategory category) {
        this.category = category;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    public FrameworkLanguage getLanguage() {
        return language;
    }

    public void setLanguage(FrameworkLanguage language) {
        this.language = language;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public String getLtsVersion() {
        return ltsVersion;
    }

    public void setLtsVersion(String ltsVersion) {
        this.ltsVersion = ltsVersion;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Set<Service> getServices() {
        return services;
    }

    public void setServices(Set<Service> services) {
        this.services = services;
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
