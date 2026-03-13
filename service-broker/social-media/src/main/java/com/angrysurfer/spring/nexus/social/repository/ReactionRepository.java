package com.angrysurfer.spring.nexus.social.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.spring.nexus.social.model.Reaction;

@Repository
public interface ReactionRepository extends MongoRepository<Reaction, String> {

}
