package com.angrysurfer.atomic.social.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.atomic.social.model.Edit;

@Repository
public interface EditRepository extends MongoRepository<Edit, String> {

//    Set<Edit> findByPostId(Post post);
}
