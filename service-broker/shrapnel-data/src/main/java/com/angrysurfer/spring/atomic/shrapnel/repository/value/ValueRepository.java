package com.angrysurfer.spring.atomic.shrapnel.repository.value;

import org.springframework.data.jpa.repository.JpaRepository;

import com.angrysurfer.spring.atomic.shrapnel.model.sqlgen.Join;

public interface ValueRepository extends JpaRepository< Join, Long > {
}
