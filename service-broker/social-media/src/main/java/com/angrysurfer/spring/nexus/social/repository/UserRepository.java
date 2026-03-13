package com.angrysurfer.spring.nexus.social.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.spring.nexus.social.model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByAlias(String alias);

    Optional<User> findByEmail(String email);

    Optional<User> findByAliasContainingIgnoreCase(String alias);
}