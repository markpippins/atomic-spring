package com.angrysurfer.atomic.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.atomic.user.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByAlias(String alias);

    Optional<User> findByEmail(String email);
}
