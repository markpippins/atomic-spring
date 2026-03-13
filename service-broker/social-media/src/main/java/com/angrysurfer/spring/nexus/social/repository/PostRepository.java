package com.angrysurfer.spring.nexus.social.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.spring.nexus.social.model.Post;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    Page<Post> findByForumId(Long forumId, Pageable pageable);

    Page<Post> findByPostedById(String userId, Pageable pageable);
}
