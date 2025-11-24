package com.angrysurfer.atomic.hostserver.controller;

import com.angrysurfer.atomic.hostserver.entity.FrameworkLanguage;
import com.angrysurfer.atomic.hostserver.repository.FrameworkLanguageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/framework-languages")
@CrossOrigin(origins = "*")
@SuppressWarnings("null")
public class FrameworkLanguageController {

    @Autowired
    private FrameworkLanguageRepository repository;

    @GetMapping
    public List<FrameworkLanguage> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FrameworkLanguage> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public FrameworkLanguage create(@RequestBody FrameworkLanguage language) {
        return repository.save(language);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FrameworkLanguage> update(@PathVariable Long id, @RequestBody FrameworkLanguage details) {
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
