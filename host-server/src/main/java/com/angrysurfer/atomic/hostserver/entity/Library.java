package com.angrysurfer.atomic.hostserver.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
@Table(name = "library")
public class Library {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "category_id")
    private Long categoryId;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", insertable = false, updatable = false)
    private LibraryCategory category;

    @Column(name = "language_id")
    private Long languageId;

    @ManyToOne
    @JoinColumn(name = "language_id", referencedColumnName = "id", insertable = false, updatable = false)
    private FrameworkLanguage language;

    @Column(name = "current_version")
    private String currentVersion;

    @Column(name = "package_name")
    private String packageName; // e.g., "three", "@angular/core", "org.springframework:spring-boot"

    @Column(name = "package_manager")
    private String packageManager; // npm, maven, pip, cargo, etc.

    @Column
    private String url; // Homepage or documentation URL

    @Column(name = "repository_url")
    private String repositoryUrl; // GitHub/GitLab URL

    @Column(name = "license")
    private String license; // MIT, Apache 2.0, etc.

    @Column(name = "active_flag")
    private Boolean activeFlag = true;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "library")
    private Set<ServiceLibrary> serviceLibraries = new HashSet<>();

    public Library() {
    }

    public Library(Long id, String name, String description, Long categoryId, LibraryCategory category, Long languageId,
            FrameworkLanguage language, String currentVersion, String packageName, String packageManager, String url,
            String repositoryUrl, String license, Boolean activeFlag, LocalDateTime createdAt, LocalDateTime updatedAt,
            Set<ServiceLibrary> serviceLibraries) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.category = category;
        this.languageId = languageId;
        this.language = language;
        this.currentVersion = currentVersion;
        this.packageName = packageName;
        this.packageManager = packageManager;
        this.url = url;
        this.repositoryUrl = repositoryUrl;
        this.license = license;
        this.activeFlag = activeFlag;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.serviceLibraries = serviceLibraries;
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public LibraryCategory getCategory() {
        return category;
    }

    public void setCategory(LibraryCategory category) {
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

    public String getPackageName() {
        return packageName;
    }

    public String getPackageManager() {
        return packageManager;
    }

    public String getUrl() {
        return url;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public String getLicense() {
        return license;
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

    public Set<ServiceLibrary> getServiceLibraries() {
        return serviceLibraries;
    }

    public void setServiceLibraries(Set<ServiceLibrary> serviceLibraries) {
        this.serviceLibraries = serviceLibraries;
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

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setPackageManager(String packageManager) {
        this.packageManager = packageManager;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Library))
            return false;
        Library library = (Library) o;
        return Objects.equals(id, library.id) &&
                Objects.equals(name, library.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
