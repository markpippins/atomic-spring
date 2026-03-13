package com.angrysurfer.spring.nexus.shrapnel.repository.style;

import com.angrysurfer.spring.nexus.shrapnel.model.db.DBField;
import com.angrysurfer.spring.nexus.shrapnel.model.style.Style;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StyleRepository extends JpaRepository< Style, Long > {

	DBField findByName(String name);
}
