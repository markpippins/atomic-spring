package com.angrysurfer.atomic.user.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.angrysurfer.atomic.broker.api.ServiceResponse;
import com.angrysurfer.atomic.broker.spi.BrokerOperation;
import com.angrysurfer.atomic.broker.spi.BrokerParam;
import com.angrysurfer.atomic.user.CommentDTO;
import com.angrysurfer.atomic.user.ReactionDTO;
import com.angrysurfer.atomic.user.model.Comment;
import com.angrysurfer.atomic.user.model.Post;
import com.angrysurfer.atomic.user.model.Reaction;
import com.angrysurfer.atomic.user.model.User;
import com.angrysurfer.atomic.user.repository.CommentRepository;
import com.angrysurfer.atomic.user.repository.PostRepository;
import com.angrysurfer.atomic.user.repository.ReactionRepository;
import com.angrysurfer.atomic.user.repository.UserRepository;

@Service
public class CommentService {
    private static final Logger log = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ReactionRepository reactionRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository,
                          UserRepository userRepository, ReactionRepository reactionRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.reactionRepository = reactionRepository;
		log.info("CommentService initialized");
    }

    @BrokerOperation("delete")
    public ServiceResponse<String> delete(@BrokerParam("commentId") Long commentId) {
        log.info("Deleting comment id {}", commentId);
        try {
            commentRepository.deleteById(commentId);
            return ServiceResponse.ok("Comment deleted successfully", "delete-" + System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error deleting comment: {}", e.getMessage());
            return (ServiceResponse<String>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Failed to delete comment: " + e.getMessage())),
                "delete-" + System.currentTimeMillis()
            );
        }
    }

    @BrokerOperation("findById")
    public ServiceResponse<CommentDTO> findById(@BrokerParam("commentId") Long commentId) {
        log.info("Find comment by id {}", commentId);
        try {
            Optional<Comment> comment = commentRepository.findById(commentId);
            if (comment.isPresent()) {
                return ServiceResponse.ok(comment.get().toDTO(), "findById-" + System.currentTimeMillis());
            }
            return (ServiceResponse<CommentDTO>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Comment " + commentId + " not found")),
                "findById-" + System.currentTimeMillis()
            );
        } catch (Exception e) {
            log.error("Error finding comment: {}", e.getMessage());
            return (ServiceResponse<CommentDTO>) ServiceResponse.error(
                List.of(java.util.Map.of("message", "Failed to find comment: " + e.getMessage())),
                "findById-" + System.currentTimeMillis()
            );
        }
    }

    @BrokerOperation("findAll")
    public ServiceResponse<Iterable<CommentDTO>> findAll() {
        log.info("Find all comments");
        try {
            Iterable<CommentDTO> comments = commentRepository.findAll().stream()
                .map(c -> c.toDTO())
                .collect(Collectors.toSet());
            return ServiceResponse.ok(comments, "findAll-" + System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error finding all comments: {}", e.getMessage());
            return (ServiceResponse<Iterable<CommentDTO>>) ServiceResponse.error(
                List.of(java.util.Map.of("message", "Failed to find comments: " + e.getMessage())),
                "findAll-" + System.currentTimeMillis()
            );
        }
    }

    public CommentDTO save(Comment n) {
        log.info("Saving comment {}", n.getId());
        return commentRepository.save(n).toDTO();
    }

    public CommentDTO save(User postedBy, String text) {
        log.info("Saving comment by user {}", postedBy.getAlias());
        return commentRepository.save(new Comment(postedBy, text)).toDTO();
    }

    @BrokerOperation("findCommentsForPost")
    public ServiceResponse<Iterable<Comment>> findCommentsForPost(@BrokerParam("postId") Long postId) {
        log.info("Find comments for post id {}", postId);
        try {
            Iterable<Comment> comments = commentRepository.findByPostId(postId);
            return ServiceResponse.ok(comments, "findCommentsForPost-" + System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error finding comments for post: {}", e.getMessage());
            return (ServiceResponse<Iterable<Comment>>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Failed to find comments for post: " + e.getMessage())),
                "findCommentsForPost-" + System.currentTimeMillis()
            );
        }
    }

    @BrokerOperation("addComment")
    public ServiceResponse<CommentDTO> addComment(@BrokerParam("data") CommentDTO data) {
        log.info("Adding comment by user {}", data.getPostedBy());
        try {
            Optional<User> user = userRepository.findByAlias(data.getPostedBy());
            
            if (user.isEmpty()) {
                return (ServiceResponse<CommentDTO>) ServiceResponse.error(
                    java.util.List.of(java.util.Map.of("message", "User not found: " + data.getPostedBy())),
                    "addComment-" + System.currentTimeMillis()
                );
            }

            if (data.getPostId() != null && data.getParentId() == null) {
                CommentDTO result = addCommentToPost(user.get(), data);
                return ServiceResponse.ok(result, "addComment-" + System.currentTimeMillis());
            } else if (data.getPostId() == null && data.getParentId() != null) {
                CommentDTO result = addReplyToComment(user.get(), data);
                return ServiceResponse.ok(result, "addComment-" + System.currentTimeMillis());
            }

            return (ServiceResponse<CommentDTO>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Invalid comment data - must specify either postId or parentId")),
                "addComment-" + System.currentTimeMillis()
            );
        } catch (Exception e) {
            log.error("Error adding comment: {}", e.getMessage());
            return (ServiceResponse<CommentDTO>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Failed to add comment: " + e.getMessage())),
                "addComment-" + System.currentTimeMillis()
            );
        }
    }

    private CommentDTO addCommentToPost(User user, CommentDTO data) throws IllegalArgumentException {
        log.info("Adding comment to post id {}", data.getPostId());
        Optional<Post> postOpt = postRepository.findById(data.getPostId());

        if (postOpt.isPresent()) {
            Post post = postOpt.get();

            Comment result = commentRepository.save(new Comment(user, data.getText(), post));

            post.getReplies().add(result);
            postRepository.save(post);

            return result.toDTO();
        }

        throw new IllegalArgumentException();
    }

    private CommentDTO addReplyToComment(User user, CommentDTO data) throws IllegalArgumentException {
        log.info("Adding reply to comment id {}", data.getParentId());
        Optional<Comment> commentOpt = commentRepository.findById(data.getParentId());

        if (commentOpt.isPresent()) {
            Comment parent = commentOpt.get();

            Comment result = commentRepository.save(new Comment(user, data.getText(), parent));

            parent.getReplies().add(result);
            save(parent);

            return result.toDTO();
        }

        throw new IllegalArgumentException();
    }



    @BrokerOperation("addReaction")
    public ServiceResponse<ReactionDTO> addReaction(@BrokerParam("commentId") Long commentId, @BrokerParam("reactionDTO") ReactionDTO reactionDTO) {
        log.info("Adding reaction to comment id {}", commentId);
        try {
            Reaction.ReactionType type = Reaction.ReactionType.valueOf(reactionDTO.getType().toUpperCase());

            Optional<User> userOpt = this.userRepository.findByAlias(reactionDTO.getAlias());
            Optional<Comment> commentOpt = commentRepository.findById(commentId);

            if (userOpt.isEmpty()) {
                return (ServiceResponse<ReactionDTO>) ServiceResponse.error(
                    java.util.List.of(java.util.Map.of("message", "User not found: " + reactionDTO.getAlias())),
                    "addReaction-" + System.currentTimeMillis()
                );
            }

            if (commentOpt.isEmpty()) {
                return (ServiceResponse<ReactionDTO>) ServiceResponse.error(
                    java.util.List.of(java.util.Map.of("message", "Comment not found: " + commentId)),
                    "addReaction-" + System.currentTimeMillis()
                );
            }

            Comment comment = commentOpt.get();
            User user = userOpt.get();

            Reaction reaction = reactionRepository.save(new Reaction(user, type));

            comment.getReactions().add(reaction);
            commentRepository.save(comment);

            return ServiceResponse.ok(reaction.toDTO(), "addReaction-" + System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error adding reaction: {}", e.getMessage());
            return (ServiceResponse<ReactionDTO>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Failed to add reaction: " + e.getMessage())),
                "addReaction-" + System.currentTimeMillis()
            );
        }
    }

    @BrokerOperation("removeReaction")
    public ServiceResponse<String> removeReaction(@BrokerParam("commentId") Long commentId, @BrokerParam("reactionDTO") ReactionDTO reactionDTO) {
        log.info("Removing reaction from comment id {}", commentId);
        try {
            Optional<Reaction> reactionOpt = this.reactionRepository.findById(reactionDTO.getId());
            Optional<Comment> commentOpt = commentRepository.findById(commentId);

            if (commentOpt.isEmpty()) {
                return (ServiceResponse<String>) ServiceResponse.error(
                    java.util.List.of(java.util.Map.of("message", "Comment not found: " + commentId)),
                    "removeReaction-" + System.currentTimeMillis()
                );
            }

            if (reactionOpt.isEmpty()) {
                return (ServiceResponse<String>) ServiceResponse.error(
                    java.util.List.of(java.util.Map.of("message", "Reaction not found: " + reactionDTO.getId())),
                    "removeReaction-" + System.currentTimeMillis()
                );
            }

            Comment comment = commentOpt.get();
            Reaction reaction = reactionOpt.get();

            comment.getReactions().remove(reaction);
            reactionRepository.delete(reaction);
            commentRepository.save(comment);

            return ServiceResponse.ok("Reaction removed successfully", "removeReaction-" + System.currentTimeMillis());
        } catch (Exception e) {
            log.error("Error removing reaction: {}", e.getMessage());
            return (ServiceResponse<String>) ServiceResponse.error(
                java.util.List.of(java.util.Map.of("message", "Failed to remove reaction: " + e.getMessage())),
                "removeReaction-" + System.currentTimeMillis()
            );
        }
    }
}
