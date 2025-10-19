package com.angrysurfer.atomic.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.atomic.user.model.Comment;

import java.util.Set;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    Set<Comment> findByPostId(String postId);

    Page<Comment> findByPostId(String postId, Pageable pageable);
}
