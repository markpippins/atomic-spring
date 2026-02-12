package com.angrysurfer.atomic.social.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.atomic.social.model.Reaction;

@Repository
public interface ReactionRepository extends MongoRepository<Reaction, String> {

}
