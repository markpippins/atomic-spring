package com.angrysurfer.atomic.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.atomic.user.model.Reaction;

@Repository
public interface ReactionRepository extends MongoRepository<Reaction, String> {

}
