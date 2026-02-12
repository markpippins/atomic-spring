package com.angrysurfer.atomic.social.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public abstract class AbstractContent implements IContent, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8556528798660585653L;

    @Id
    private String id;

    private LocalDateTime created;

    private LocalDateTime updated;

    private String text;

    private String url;

    private Long rating;

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

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

	@Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

	@Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

	@Override
    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

	@Override
    public String getPostedDate() {
        DateTimeFormatter newPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return this.created.format(newPattern);
    }
}