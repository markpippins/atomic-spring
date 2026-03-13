package com.angrysurfer.spring.nexus.shrapnel.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.angrysurfer.spring.nexus.shrapnel.model.db.DBField;

public interface FieldRepository extends JpaRepository< DBField, Long> {

    DBField findByName(String name);
}
