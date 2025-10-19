package com.angrysurfer.atomic.user.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.atomic.user.model.Edit;

@Repository
public interface EditRepository extends MongoRepository<Edit, String> {

//    Set<Edit> findByPostId(Post post);
}
