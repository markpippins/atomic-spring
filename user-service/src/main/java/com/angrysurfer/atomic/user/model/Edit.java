package com.angrysurfer.atomic.user.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "Edit")
//@Table(schema = "social")
public class Edit implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7243938370276557466L;

    @Id
    @SequenceGenerator(name = "edit_sequence", sequenceName = "edit_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "edit_sequence")
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;

    @CreationTimestamp
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime updated;

    @Lob
    private String text;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Edit() {
    }

    public Edit(String previous) {
        this.setText(previous);
    }

}
