package com.angrysurfer.atomic.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.atomic.user.model.UserRegistration;

import java.util.Optional;

@Repository
public interface UserRegistrationRepository extends MongoRepository<UserRegistration, String> {
    Optional<UserRegistration> findByAlias(String alias);

    Optional<UserRegistration> findByEmail(String email);
}
