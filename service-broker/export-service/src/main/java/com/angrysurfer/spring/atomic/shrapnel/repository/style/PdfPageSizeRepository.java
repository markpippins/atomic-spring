package com.angrysurfer.spring.atomic.shrapnel.repository.style;

import com.angrysurfer.spring.atomic.shrapnel.model.style.PdfPageSize;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PdfPageSizeRepository extends JpaRepository< PdfPageSize, Integer> {
    PdfPageSize findByName(String name);
}
