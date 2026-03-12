package com.angrysurfer.spring.atomic.shrapnel.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.angrysurfer.spring.atomic.shrapnel.model.db.DBDataSource;

public interface DataSourceRepository extends JpaRepository< DBDataSource, Long> {
    DBDataSource findByName(String name);
}
