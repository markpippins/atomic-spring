package com.angrysurfer.spring.atomic.shrapnel.repository.value;

import org.springframework.data.jpa.repository.JpaRepository;

import com.angrysurfer.spring.atomic.shrapnel.model.value.StringValue;

public interface StringValueRepository extends JpaRepository< StringValue, Long > {
}
