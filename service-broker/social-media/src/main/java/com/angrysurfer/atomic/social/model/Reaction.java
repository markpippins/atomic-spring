package com.angrysurfer.atomic.social.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import com.angrysurfer.atomic.social.ReactionDTO;
import com.angrysurfer.atomic.social.model.User;

import java.io.Serializable;
import java.time.LocalDateTime;

@Document(collection = "reactions")
public class Reaction implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2157436062288147245L;

    public ReactionDTO toDTO() {
        ReactionDTO dto = new ReactionDTO();
        dto.setId(getId()); // MongoDB ObjectId as String
        dto.setType(getReactionType().toString());
        dto.setAlias(getUser().getAlias());
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
    private String id;

    private LocalDateTime created;

    private ReactionType reactionType;

    @DBRef
    private User user;
    
    @DBRef
    private Post post;

    @DBRef
    private Comment comment;

    public Reaction() {

    }

    public Reaction(User user, ReactionType type) {
        this.user = user;
        this.reactionType = type;
    }
    
    public Post getPost() {
        return post;
    }
    
    public void setPost(Post post) {
        this.post = post;
    }
    
    public Comment getComment() {
        return comment;
    }
    
    public void setComment(Comment comment) {
        this.comment = comment;
    }

}