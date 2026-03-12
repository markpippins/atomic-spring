package com.angrysurfer.spring.atomic.shrapnel.repository.sqlgen;

import com.angrysurfer.spring.atomic.shrapnel.model.sqlgen.Join;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JoinRepository extends JpaRepository< Join, Long > {
}
