package com.angrysurfer.atomic.hostserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "service_configurations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(nullable = false)
    private String configKey;

    @Column(length = 4000)
    private String configValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConfigEnvironment environment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConfigType type = ConfigType.STRING;

    @Column
    private Boolean isSecret = false;

    @Column(length = 1000)
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
        if (!(o instanceof ServiceConfiguration)) return false;
        ServiceConfiguration that = (ServiceConfiguration) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public enum ConfigEnvironment {
        ALL, DEVELOPMENT, STAGING, PRODUCTION, TEST
    }

    public enum ConfigType {
        STRING, NUMBER, BOOLEAN, JSON, URL, DATABASE_URL, API_KEY
    }
}
