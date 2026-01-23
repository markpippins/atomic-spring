package com.angrysurfer.atomic.hostserver.entity;

import java.time.LocalDateTime;
import java.util.Objects;

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
@Table(name = "service_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @ManyToOne
    @JoinColumn(name = "service_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Service service;

    @Column(name = "config_type_id", nullable = false)
    private Long configTypeId;

    @Column(length = 1000)
    private String description;

    @Column(name = "config_key", nullable = false)
    private String configKey;

    @Column(name = "config_value", nullable = false)
    private String configValue;

    @Column(name = "environment_id", nullable = false)
    private Long environmentId;

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
        if (!(o instanceof ServiceConfiguration))
            return false;
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

    // Methods needed for backward compatibility with controllers and services
    public ConfigEnvironment getEnvironment() {
        // This field was removed, returning null for now
        return null;
    }

    public void setEnvironment(ConfigEnvironment environment) {
        // This field was removed, doing nothing for now
    }

    public ConfigType getType() {
        // This field was removed, returning null for now
        return null;
    }

    public void setType(ConfigType type) {
        // This field was removed, doing nothing for now
    }

    public Boolean getIsSecret() {
        // This field was removed, returning false for now
        return false;
    }

    public void setIsSecret(Boolean isSecret) {
        // This field was removed, doing nothing for now
    }

    public Service getService() {
        // This field was removed, returning null for now
        return null;
    }

    public void setService(Service service) {
        // This field was removed, doing nothing for now
    }

    public void setConfigTypeId(Long configTypeId) {
        this.configTypeId = configTypeId;
    }

    public void setEnvironmentId(Long environmentId) {
        this.environmentId = environmentId;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
