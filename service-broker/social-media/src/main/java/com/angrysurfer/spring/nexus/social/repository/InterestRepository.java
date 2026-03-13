package com.angrysurfer.spring.nexus.social.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.spring.nexus.social.model.Interest;

import java.util.Optional;

@Repository
public interface InterestRepository extends MongoRepository<Interest, String> {

    Optional<Interest> findByName(String name);

}