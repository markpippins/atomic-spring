package com.angrysurfer.atomic.hostserver.controller;

import com.angrysurfer.atomic.hostserver.entity.Framework;
import com.angrysurfer.atomic.hostserver.repository.FrameworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/frameworks")
@CrossOrigin(origins = "*")
@SuppressWarnings("null")
public class FrameworkController {
    
    @Autowired
    private FrameworkRepository frameworkRepository;
    
    @GetMapping
    public List<Framework> getAllFrameworks() {
        return frameworkRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Framework> getFrameworkById(@PathVariable Long id) {
        return frameworkRepository.findById(id)
            .map(framework -> ResponseEntity.ok(framework))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<Framework> getFrameworkByName(@PathVariable String name) {
        return frameworkRepository.findByName(name)
            .map(framework -> ResponseEntity.ok(framework))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @Autowired
    private com.angrysurfer.atomic.hostserver.repository.FrameworkCategoryRepository frameworkCategoryRepository;

    @Autowired
    private com.angrysurfer.atomic.hostserver.repository.FrameworkLanguageRepository frameworkLanguageRepository;

    @GetMapping("/category/{categoryId}")
    public List<Framework> getFrameworksByCategory(@PathVariable Long categoryId) {
        return frameworkCategoryRepository.findById(categoryId)
                .map(category -> frameworkRepository.findByCategory(category))
                .orElse(List.of());
    }
    
    @GetMapping("/language/{languageId}")
    public List<Framework> getFrameworksByLanguage(@PathVariable Long languageId) {
        return frameworkLanguageRepository.findById(languageId)
                .map(language -> frameworkRepository.findByLanguage(language))
                .orElse(List.of());
    }
    
    @GetMapping("/broker-compatible")
    public List<Framework> getBrokerCompatibleFrameworks() {
        return frameworkRepository.findBySupportsBrokerPattern(true);
    }
    
    @PostMapping
    public ResponseEntity<Framework> createFramework(@RequestBody Framework framework) {
        Framework savedFramework = frameworkRepository.save(framework);
        return new ResponseEntity<>(savedFramework, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Framework> updateFramework(@PathVariable Long id, @RequestBody Framework frameworkDetails) {
        return frameworkRepository.findById(id)
            .map(framework -> {
                framework.setName(frameworkDetails.getName());
                framework.setDescription(frameworkDetails.getDescription());
                framework.setCategory(frameworkDetails.getCategory());
                framework.setLanguage(frameworkDetails.getLanguage());
                framework.setLatestVersion(frameworkDetails.getLatestVersion());
                framework.setDocumentationUrl(frameworkDetails.getDocumentationUrl());
                framework.setRepositoryUrl(frameworkDetails.getRepositoryUrl());
                framework.setSupportsBrokerPattern(frameworkDetails.getSupportsBrokerPattern());
                return ResponseEntity.ok(frameworkRepository.save(framework));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFramework(@PathVariable Long id) {
        return frameworkRepository.findById(id)
            .map(framework -> {
                frameworkRepository.delete(framework);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
