package com.angrysurfer.atomic.user.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;

import java.util.stream.Collectors;

import com.angrysurfer.atomic.user.PostDTO;
import com.angrysurfer.atomic.user.PostStatDTO;

@Entity(name = "Post")
public class Post extends AbstractContent {

    /**
     *
     */
    private static final long serialVersionUID = -6085955136753566931L;

    @Id
    @SequenceGenerator(name = "post_sequence", sequenceName = "post_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_sequence")
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private User postedBy;

    @OneToOne(fetch = FetchType.EAGER)
    private User postedTo;

    private Long forumId;

    private String sourceUrl;

    private String title;

    public PostDTO toDTO() {
        PostDTO dto = new PostDTO();
        dto.setId(getId());
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
        dto.setId(getId());
        dto.setRating(getRating());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "post_comment", joinColumns = {
        @JoinColumn(name = "comment_id")}, inverseJoinColumns = {
        @JoinColumn(name = "post_id")})
    private Set<Comment> replies = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "post_edit", joinColumns = {
        @JoinColumn(name = "edit_id")}, inverseJoinColumns = {
        @JoinColumn(name = "post_id")})
    private Set<Edit> edits = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "post_reaction", joinColumns = {
        @JoinColumn(name = "reaction_id")}, inverseJoinColumns = {
        @JoinColumn(name = "post_id")})
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
