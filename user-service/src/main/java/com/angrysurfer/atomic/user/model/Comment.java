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
import jakarta.persistence.SequenceGenerator;



import java.util.stream.Collectors;

import com.angrysurfer.atomic.user.CommentDTO;

@Entity
//@Table(schema = "social")
public class Comment extends AbstractContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1902851597891565438L;

	@Id
	@SequenceGenerator(name = "comment_sequence", sequenceName = "comment_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_sequence")
	@Column(name = "id", updatable = false, nullable = false, unique = true)
	private Long id;

	@ManyToOne
	protected Comment parent;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "posted_by_user_id")
	private User postedBy;

	@ManyToOne(fetch = FetchType.LAZY)
	private Post post;

	@OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
	private Set<Comment> replies = new HashSet<>();

	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "comment_reaction", joinColumns = { @JoinColumn(name = "reaction_id") }, inverseJoinColumns = {
			@JoinColumn(name = "comment_id") })
	private Set<Reaction> reactions = new HashSet<>();

	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "comment_edit", joinColumns = { @JoinColumn(name = "edit_id") }, inverseJoinColumns = {
			@JoinColumn(name = "comment_id") })
	private Set<Edit> edits = new HashSet<>();

    public CommentDTO toDTO() {
        CommentDTO dto = new CommentDTO();
        dto.setId(getId());
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
