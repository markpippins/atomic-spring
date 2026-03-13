package com.angrysurfer.spring.nexus.shrapnel.repository.value;

import org.springframework.data.jpa.repository.JpaRepository;

import com.angrysurfer.spring.nexus.shrapnel.model.value.LongValue;

public interface LongValueRepository extends JpaRepository< LongValue, Long > {
}
