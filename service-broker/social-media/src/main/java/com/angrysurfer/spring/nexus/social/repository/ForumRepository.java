package com.angrysurfer.spring.nexus.social.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.spring.nexus.social.model.Forum;

import java.util.Optional;

@Repository
public interface ForumRepository extends MongoRepository<Forum, String> {

    Optional<Forum> findByName(String name);

}
