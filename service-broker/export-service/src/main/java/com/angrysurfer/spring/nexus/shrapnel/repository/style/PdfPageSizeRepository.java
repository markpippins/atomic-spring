package com.angrysurfer.spring.nexus.shrapnel.repository.style;

import org.springframework.data.jpa.repository.JpaRepository;

import com.angrysurfer.spring.nexus.shrapnel.model.style.PdfPageSize;

public interface PdfPageSizeRepository extends JpaRepository< PdfPageSize, Integer> {
    PdfPageSize findByName(String name);
}
