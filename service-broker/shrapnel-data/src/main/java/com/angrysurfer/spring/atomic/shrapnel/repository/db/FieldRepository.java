package com.angrysurfer.spring.atomic.shrapnel.repository.db;

import com.angrysurfer.spring.atomic.shrapnel.model.db.DBField;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldRepository extends JpaRepository< DBField, Long> {

    DBField findByName(String name);
}
