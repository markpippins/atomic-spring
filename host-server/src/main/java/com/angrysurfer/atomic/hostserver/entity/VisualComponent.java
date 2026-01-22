package com.angrysurfer.atomic.hostserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "visual_components")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisualComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String type; // The unique slug, e.g., 'rest-api'

    @Column(nullable = false)
    private String name; // Display label

    @Column(columnDefinition = "TEXT")
    private String description;

    // 3D Visual Properties
    @Column(nullable = false)
    private String geometry; // sphere, cylinder, etc.

    @Column(name = "default_color", nullable = false)
    private Long defaultColor; // Hex color as number

    @Column(nullable = false)
    private Double scale;

    // UI Palette Properties
    @Column(name = "icon_class")
    private String iconClass;

    @Column(name = "color_class")
    private String colorClass;

    @Column(name = "is_system")
    private Boolean isSystem = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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
}
