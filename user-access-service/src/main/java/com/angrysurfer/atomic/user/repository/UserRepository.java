package com.angrysurfer.atomic.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.atomic.user.model.ValidUser;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<ValidUser, String> {
    Optional<ValidUser> findByAlias(String alias);

    Optional<ValidUser> findByEmail(String email);
}
