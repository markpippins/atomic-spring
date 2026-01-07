package com.angrysurfer.atomic.hostserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "service_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "active_flag")
    private Boolean activeFlag = true;

    // Methods needed for backward compatibility with controllers and services
    public String getDescription() {
        // This field was removed, returning null for now
        return null;
    }

    public void setDescription(String description) {
        // This field was removed, doing nothing for now
    }
}
