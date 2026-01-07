package com.angrysurfer.atomic.hostserver.controller;

import com.angrysurfer.atomic.hostserver.client.ServicesConsoleClient;
import com.angrysurfer.atomic.hostserver.entity.Framework;
import com.angrysurfer.atomic.hostserver.repository.FrameworkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/frameworks")
@CrossOrigin(origins = "*")
public class FrameworkController {

    private static final Logger log = LoggerFactory.getLogger(FrameworkController.class);

    private final ServicesConsoleClient client;
    private final FrameworkRepository frameworkRepository;

    public FrameworkController(ServicesConsoleClient client, FrameworkRepository frameworkRepository) {
        this.client = client;
        this.frameworkRepository = frameworkRepository;
    }

    @GetMapping
    public List<Framework> getAllFrameworks() {
        log.info("Fetching all frameworks from database");
        return frameworkRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Framework> getFrameworkById(@PathVariable Long id) {
        log.info("Fetching framework by ID: {}", id);
        Optional<Framework> framework = frameworkRepository.findById(id);
        return framework.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Framework> getFrameworkByName(@PathVariable String name) {
        log.info("Fetching framework by name: {}", name);
        Optional<Framework> framework = frameworkRepository.findByName(name);
        return framework.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/broker-compatible")
    public List<Framework> getBrokerCompatibleFrameworks() {
        // Filter frameworks that support broker pattern
        return frameworkRepository.findAll().stream()
                .filter(framework -> framework.getSupportsBrokerPattern() != null && framework.getSupportsBrokerPattern())
                .toList();
    }

    // Create/Update/Delete - will be implemented as needed
}
