package com.angrysurfer.spring.atomic.shrapnel.repository;

import com.angrysurfer.spring.atomic.shrapnel.model.DBExport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExportRepository extends JpaRepository< DBExport, Long> {
    DBExport findByName(String name);
}
