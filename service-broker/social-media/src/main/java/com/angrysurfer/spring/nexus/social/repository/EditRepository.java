package com.angrysurfer.spring.nexus.social.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.spring.nexus.social.model.Edit;

@Repository
public interface EditRepository extends MongoRepository<Edit, String> {

    // Set<Edit> findByPostId(Post post);
}
