package com.angrysurfer.spring.atomic.shrapnel.repository.sqlgen;

import com.angrysurfer.spring.atomic.shrapnel.model.sqlgen.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueryRepository extends JpaRepository< Query, Long > {
}
