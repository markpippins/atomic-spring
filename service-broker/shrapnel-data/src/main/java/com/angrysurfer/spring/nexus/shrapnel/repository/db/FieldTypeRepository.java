package com.angrysurfer.spring.nexus.shrapnel.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.angrysurfer.spring.nexus.shrapnel.model.db.DBFieldType;

public interface FieldTypeRepository extends JpaRepository< DBFieldType, Integer> {

}
