package com.angrysurfer.spring.nexus.shrapnel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.angrysurfer.spring.nexus.shrapnel.model.DBExport;

public interface ExportRepository extends JpaRepository< DBExport, Long> {
    DBExport findByName(String name);
}
