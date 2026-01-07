package com.angrysurfer.atomic.hostserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "languages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FrameworkLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column
    private String url;

    @Column(name = "current_version")
    private String currentVersion;

    @Column(name = "lts_version")
    private String ltsVersion;

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
