package com.angrysurfer.atomic.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.angrysurfer.atomic.user.model.Reaction;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

}
