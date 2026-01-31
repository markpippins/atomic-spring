package com.angrysurfer.atomic.hostserver.entity;

import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_types")
public class ServiceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "active_flag")
    private Boolean activeFlag = true;

    @Column(name = "default_component_id")
    private Long defaultComponentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "default_component_id", referencedColumnName = "id", insertable = false, updatable = false)
    private VisualComponent defaultComponent;

    public ServiceType() {
    }

    public ServiceType(Long id, String name, Boolean activeFlag, Long defaultComponentId,
            VisualComponent defaultComponent) {
        this.id = id;
        this.name = name;
        this.activeFlag = activeFlag;
        this.defaultComponentId = defaultComponentId;
        this.defaultComponent = defaultComponent;
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

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Long getDefaultComponentId() {
        return defaultComponentId;
    }

    public void setDefaultComponentId(Long defaultComponentId) {
        this.defaultComponentId = defaultComponentId;
    }

    public VisualComponent getDefaultComponent() {
        return defaultComponent;
    }

    public void setDefaultComponent(VisualComponent defaultComponent) {
        this.defaultComponent = defaultComponent;
    }

    // Methods needed for backward compatibility with controllers and services
    public String getDescription() {
        // This field was removed, returning null for now
        return null;
    }

    public void setDescription(String description) {
        // This field was removed, doing nothing for now
    }
}
