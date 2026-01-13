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

/**
 * Represents a reusable software library that can be used as a compile-time dependency.
 * Examples: Three.js, Lodash, RxJS, Zod, Prisma, TailwindCSS
 */
@Entity
@Table(name = "libraries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({ "serviceLibraries" })
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
        if (!(o instanceof Library)) return false;
        Library library = (Library) o;
        return Objects.equals(id, library.id) &&
                Objects.equals(name, library.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
