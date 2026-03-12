package com.angrysurfer.spring.atomic.shrapnel.repository.sqlgen;

import com.angrysurfer.spring.atomic.shrapnel.model.sqlgen.SqlColumn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColumnRepository extends JpaRepository< SqlColumn, Long > {
}
