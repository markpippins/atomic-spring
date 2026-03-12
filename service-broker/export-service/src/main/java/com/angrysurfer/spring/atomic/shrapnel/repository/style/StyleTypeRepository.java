package com.angrysurfer.spring.atomic.shrapnel.repository.style;

import com.angrysurfer.spring.atomic.shrapnel.model.style.StyleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StyleTypeRepository extends JpaRepository< StyleType, Integer > {

	StyleType findByName(String name);
}
