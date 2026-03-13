package com.angrysurfer.spring.nexus.social.model;

import java.util.Set;

import com.angrysurfer.spring.nexus.social.model.User;

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
