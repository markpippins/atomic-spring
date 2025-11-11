package com.angrysurfer.atomic.user.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.stream.Collectors;

import com.angrysurfer.atomic.user.CommentDTO;

@Document(collection = "comments")
public class Comment extends AbstractContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1902851597891565438L;

	private String id;

	@DBRef
	protected Comment parent;

	@DBRef
	private User postedBy;

	@DBRef
	private Post post;

	@DBRef
	private Set<Comment> replies = new HashSet<>();

	@DBRef
	private Set<Reaction> reactions = new HashSet<>();

	@DBRef
	private Set<Edit> edits = new HashSet<>();

    public CommentDTO toDTO() {
        CommentDTO dto = new CommentDTO();
        dto.setId(getId()); // MongoDB ObjectId as String
        dto.setText(getText());
        dto.setPostedBy(getPostedBy().getAlias());
        dto.setPostId(getPost().getId());
        if (getParent() != null) {
            dto.setParentId(getParent().getId());
        }
        dto.setReplies(getReplies().stream().map(r -> r.toDTO()).collect(Collectors.toSet()));
        dto.setReactions(getReactions().stream().map(r -> r.toDTO()).collect(Collectors.toSet()));
        return dto;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Comment getParent() {
		return parent;
	}

	public void setParent(Comment parent) {
		this.parent = parent;
	}

	public User getPostedBy() {
		return postedBy;
	}

	public void setPostedBy(User postedBy) {
		this.postedBy = postedBy;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public Set<Comment> getReplies() {
		return replies;
	}

	public void setReplies(Set<Comment> replies) {
		this.replies = replies;
	}

	public Set<Reaction> getReactions() {
		return reactions;
	}

	public void setReactions(Set<Reaction> reactions) {
		this.reactions = reactions;
	}

	public Set<Edit> getEdits() {
		return edits;
	}

	public void setEdits(Set<Edit> edits) {
		this.edits = edits;
	}

	public Comment() {
	}

	public Comment(User user, String text) {
		setText(text);
		setPostedBy(user);
	}

	public Comment(User user, String text, Post post) {
		this(user, text);
		setPost(post);
	}

	public Comment(User user, String text, Comment parent) {
		this(user, text);
		setParent(parent);
	}

}