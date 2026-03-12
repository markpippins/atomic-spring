package com.angrysurfer.spring.atomic.shrapnel.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.angrysurfer.spring.atomic.shrapnel.model.db.DBFieldType;

public interface FieldTypeRepository extends JpaRepository< DBFieldType, Integer> {

}
