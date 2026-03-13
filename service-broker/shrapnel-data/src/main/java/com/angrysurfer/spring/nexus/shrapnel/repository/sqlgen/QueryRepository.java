package com.angrysurfer.spring.nexus.shrapnel.repository.sqlgen;

import org.springframework.data.jpa.repository.JpaRepository;

import com.angrysurfer.spring.nexus.shrapnel.model.sqlgen.Query;

public interface QueryRepository extends JpaRepository< Query, Long > {
}
