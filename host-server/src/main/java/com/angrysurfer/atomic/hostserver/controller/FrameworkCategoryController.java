package com.angrysurfer.atomic.hostserver.controller;

import com.angrysurfer.atomic.hostserver.entity.FrameworkCategory;
import com.angrysurfer.atomic.hostserver.repository.FrameworkCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/framework-categories")
@CrossOrigin(origins = "*")
@SuppressWarnings("null")
public class FrameworkCategoryController {

    @Autowired
    private FrameworkCategoryRepository repository;

    @GetMapping
    public List<FrameworkCategory> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FrameworkCategory> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public FrameworkCategory create(@RequestBody FrameworkCategory category) {
        return repository.save(category);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FrameworkCategory> update(@PathVariable Long id, @RequestBody FrameworkCategory details) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setName(details.getName());
                    existing.setDescription(details.getDescription());
                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return repository.findById(id)
                .map(existing -> {
                    repository.delete(existing);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
