package com.angrysurfer.spring.nexus.shrapnel.repository.style;

import org.springframework.data.jpa.repository.JpaRepository;

import com.angrysurfer.spring.nexus.shrapnel.model.style.StyleType;

public interface StyleTypeRepository extends JpaRepository< StyleType, Integer > {

	StyleType findByName(String name);
}
