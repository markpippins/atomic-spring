package com.angrysurfer.atomic.user.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.stream.Collectors;

import com.angrysurfer.atomic.user.PostDTO;
import com.angrysurfer.atomic.user.PostStatDTO;

@Document(collection = "posts")
public class Post extends AbstractContent {

    /**
     *
     */
    private static final long serialVersionUID = -6085955136753566931L;

    private String id;

    @DBRef
    private User postedBy;

    @DBRef
    private User postedTo;

    private Long forumId;

    private String sourceUrl;

    private String title;

    public PostDTO toDTO() {
        PostDTO dto = new PostDTO();
        dto.setId(getId()); // MongoDB ObjectId as String
        dto.setText(getText());
        dto.setPostedBy(getPostedBy().getAlias());
        dto.setForumId(getForumId());
        if (getPostedTo() != null) {
            dto.setPostedTo(getPostedTo().getAlias());
        }
        dto.setReplies(getReplies().stream().map(r -> r.toDTO()).collect(Collectors.toSet()));
        dto.setReactions(getReactions().stream().map(r -> r.toDTO()).collect(Collectors.toSet()));
        return dto;
    }

    public PostStatDTO toStatDTO() {
        PostStatDTO dto = new PostStatDTO();
        dto.setId(getId() != null ? Long.valueOf(getId().hashCode()) : null); // MongoDB ObjectId to Long
        dto.setRating(getRating());
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(User postedBy) {
        this.postedBy = postedBy;
    }

    public User getPostedTo() {
        return postedTo;
    }

    public void setPostedTo(User postedTo) {
        this.postedTo = postedTo;
    }

    public Long getForumId() {
        return forumId;
    }

    public void setForumId(Long forumId) {
        this.forumId = forumId;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @DBRef
    private Set<Comment> replies = new HashSet<>();

    @DBRef
    private Set<Edit> edits = new HashSet<>();

    @DBRef
    private Set<Reaction> reactions = new HashSet<>();

    public Post() {

    }

    public Post(User postedBy, User postedTo, String text) {
        this.postedBy = postedBy;
        this.postedTo = postedTo;
        this.setText(text);
    }

    @Override
    public Set<Edit> getEdits() {
        return edits;
    }

    @Override
    public Set<Reaction> getReactions() {
        return reactions;
    }

    @Override
    public Set<Comment> getReplies() {
        return replies;
    }

    public void setEdits(Set<Edit> edits) {
        this.edits = edits;
    }

    public void setReactions(Set<Reaction> reactions) {
        this.reactions = reactions;
    }

    public void setReplies(Set<Comment> replies) {
        this.replies = replies;
    }

}