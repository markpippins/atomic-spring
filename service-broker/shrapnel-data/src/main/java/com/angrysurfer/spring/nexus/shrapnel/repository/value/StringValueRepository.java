package com.angrysurfer.spring.nexus.shrapnel.repository.value;

import org.springframework.data.jpa.repository.JpaRepository;

import com.angrysurfer.spring.nexus.shrapnel.model.value.StringValue;

public interface StringValueRepository extends JpaRepository< StringValue, Long > {
}
