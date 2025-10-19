package com.angrysurfer.atomic.user.model;

import java.util.Set;

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
