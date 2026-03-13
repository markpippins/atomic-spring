package com.angrysurfer.spring.nexus.shrapnel.repository.sqlgen;

import org.springframework.data.jpa.repository.JpaRepository;

import com.angrysurfer.spring.nexus.shrapnel.model.sqlgen.SqlTable;

public interface TableRepository extends JpaRepository< SqlTable, Long > {
}
