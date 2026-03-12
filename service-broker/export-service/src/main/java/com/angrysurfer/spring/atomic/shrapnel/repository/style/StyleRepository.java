package com.angrysurfer.spring.atomic.shrapnel.repository.style;

import com.angrysurfer.spring.atomic.shrapnel.model.db.DBField;
import com.angrysurfer.spring.atomic.shrapnel.model.style.Style;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StyleRepository extends JpaRepository< Style, Long > {

	DBField findByName(String name);
}
