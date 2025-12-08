package com.angrysurfer.atomic.hostserver.controller;

import com.angrysurfer.atomic.hostserver.entity.Framework;
import com.angrysurfer.atomic.hostserver.repository.FrameworkRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(FrameworkController.class);
    
    @Autowired
    private FrameworkRepository frameworkRepository;
    
    @GetMapping
    public List<Framework> getAllFrameworks() {
        log.info("Fetching all frameworks");
        List<Framework> frameworks = frameworkRepository.findAll();
        log.debug("Fetched {} frameworks", frameworks.size());
        return frameworks;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Framework> getFrameworkById(@PathVariable Long id) {
        log.info("Fetching framework by ID: {}", id);
        return frameworkRepository.findById(id)
            .map(framework -> {
                log.debug("Framework found with ID: {}", id);
                return ResponseEntity.ok(framework);
            })
            .orElseGet(() -> {
                log.warn("Framework not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            });
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<Framework> getFrameworkByName(@PathVariable String name) {
        log.info("Fetching framework by name: {}", name);
        return frameworkRepository.findByName(name)
            .map(framework -> {
                log.debug("Framework found with name: {}", name);
                return ResponseEntity.ok(framework);
            })
            .orElseGet(() -> {
                log.warn("Framework not found with name: {}", name);
                return ResponseEntity.notFound().build();
            });
    }
    
    @Autowired
    private com.angrysurfer.atomic.hostserver.repository.FrameworkCategoryRepository frameworkCategoryRepository;

    @Autowired
    private com.angrysurfer.atomic.hostserver.repository.FrameworkLanguageRepository frameworkLanguageRepository;

    @GetMapping("/category/{categoryId}")
    public List<Framework> getFrameworksByCategory(@PathVariable Long categoryId) {
        log.info("Fetching frameworks by category ID: {}", categoryId);
        return frameworkCategoryRepository.findById(categoryId)
                .map(category -> {
                    List<Framework> frameworks = frameworkRepository.findByCategory(category);
                    log.debug("Fetched {} frameworks for category ID: {}", frameworks.size(), categoryId);
                    return frameworks;
                })
                .orElseGet(() -> {
                    log.warn("Category not found with ID: {}", categoryId);
                    return List.of();
                });
    }
    
    @GetMapping("/language/{languageId}")
    public List<Framework> getFrameworksByLanguage(@PathVariable Long languageId) {
        log.info("Fetching frameworks by language ID: {}", languageId);
        return frameworkLanguageRepository.findById(languageId)
                .map(language -> {
                    List<Framework> frameworks = frameworkRepository.findByLanguage(language);
                    log.debug("Fetched {} frameworks for language ID: {}", frameworks.size(), languageId);
                    return frameworks;
                })
                .orElseGet(() -> {
                    log.warn("Language not found with ID: {}", languageId);
                    return List.of();
                });
    }
    
    @GetMapping("/broker-compatible")
    public List<Framework> getBrokerCompatibleFrameworks() {
        log.info("Fetching broker-compatible frameworks");
        List<Framework> frameworks = frameworkRepository.findBySupportsBrokerPattern(true);
        log.debug("Fetched {} broker-compatible frameworks", frameworks.size());
        return frameworks;
    }
    
    @PostMapping
    public ResponseEntity<Framework> createFramework(@RequestBody Framework framework) {
        log.info("Creating framework with name: {}", framework.getName());
        try {
            Framework savedFramework = frameworkRepository.save(framework);
            log.info("Framework created successfully with ID: {}", savedFramework.getId());
            return new ResponseEntity<>(savedFramework, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating framework: {}", framework.getName(), e);
            throw e;
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Framework> updateFramework(@PathVariable Long id, @RequestBody Framework frameworkDetails) {
        log.info("Updating framework with ID: {}", id);
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
                Framework updatedFramework = frameworkRepository.save(framework);
                log.info("Framework updated successfully with ID: {}", updatedFramework.getId());
                return ResponseEntity.ok(updatedFramework);
            })
            .orElseGet(() -> {
                log.warn("Framework not found with ID: {} for update", id);
                return ResponseEntity.notFound().build();
            });
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFramework(@PathVariable Long id) {
        log.info("Deleting framework with ID: {}", id);
        return frameworkRepository.findById(id)
            .map(framework -> {
                frameworkRepository.delete(framework);
                log.info("Framework deleted successfully with ID: {}", id);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElseGet(() -> {
                log.warn("Framework not found with ID: {} for deletion", id);
                return ResponseEntity.notFound().build();
            });
    }
}
