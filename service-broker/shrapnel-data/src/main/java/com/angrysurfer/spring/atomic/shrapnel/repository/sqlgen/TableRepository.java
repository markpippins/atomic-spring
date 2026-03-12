package com.angrysurfer.spring.atomic.shrapnel.repository.sqlgen;

import org.springframework.data.jpa.repository.JpaRepository;

import com.angrysurfer.spring.atomic.shrapnel.model.sqlgen.SqlTable;

public interface TableRepository extends JpaRepository< SqlTable, Long > {
}
