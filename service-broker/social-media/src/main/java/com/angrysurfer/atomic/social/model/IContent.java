package com.angrysurfer.atomic.social.model;

import java.util.Set;

import com.angrysurfer.atomic.social.model.User;

public interface IContent {

    String getId();

    Set<Edit> getEdits();

    User getPostedBy();

    String getPostedDate();

    Long getRating();

    Set<Reaction> getReactions();

    Set<Comment> getReplies();

    String getText();

    String getUrl();
}
