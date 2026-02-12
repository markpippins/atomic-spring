package com.angrysurfer.atomic.social.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.atomic.social.model.Forum;

import java.util.Optional;

@Repository
public interface ForumRepository extends MongoRepository<Forum, String> {

    Optional<Forum> findByName(String name);

}
