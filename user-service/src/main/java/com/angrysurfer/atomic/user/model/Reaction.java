package com.angrysurfer.atomic.user.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import com.angrysurfer.atomic.user.ReactionDTO;

import java.io.Serializable;
import java.time.LocalDateTime;

//@Table(schema = "social")
@Entity(name = "Reaction")
public class Reaction implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2157436062288147245L;

    public ReactionDTO toDTO() {
        ReactionDTO dto = new ReactionDTO();
        dto.setId(getId());
        dto.setType(getReactionType().toString());
        dto.setAlias(getUser().getAlias());
        return dto;
    }

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

    public ReactionType getReactionType() {
        return reactionType;
    }

    public void setReactionType(ReactionType reactionType) {
        this.reactionType = reactionType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public enum ReactionType {
        LIKE, LOVE, ANGER, SADNESS, SURPRISE
    }

    @Id
    @SequenceGenerator(name = "reaction_sequence", sequenceName = "reaction_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reaction_sequence")
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;

    @CreationTimestamp
    private LocalDateTime created;

    @Enumerated(EnumType.STRING)
    private ReactionType reactionType;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    public Reaction() {

    }

    public Reaction(User user, ReactionType type) {
        this.user = user;
        this.reactionType = type;
    }

}
