package com.angrysurfer.atomic.social.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.io.Serializable;
import java.time.LocalDateTime;

@Document(collection = "edits")
public class Edit implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7243938370276557466L;

    @Id
    private String id;

    private LocalDateTime created;

    private LocalDateTime updated;

    private String text;
    
    @DBRef
    private Post post;

    @DBRef
    private Comment comment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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