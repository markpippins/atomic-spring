package com.angrysurfer.atomic.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.atomic.user.model.Profile;

import java.util.Optional;

@Repository
public interface ProfileRepository extends MongoRepository<Profile, String> {

    Optional<Profile> findByUserId(String userId);

    void deleteByUserId(String userId);
}
