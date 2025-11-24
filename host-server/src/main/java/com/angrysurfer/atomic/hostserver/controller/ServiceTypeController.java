package com.angrysurfer.atomic.hostserver.controller;

import com.angrysurfer.atomic.hostserver.entity.ServiceType;
import com.angrysurfer.atomic.hostserver.repository.ServiceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-types")
@CrossOrigin(origins = "*")
@SuppressWarnings("null")
public class ServiceTypeController {

    @Autowired
    private ServiceTypeRepository repository;

    @GetMapping
    public List<ServiceType> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceType> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ServiceType create(@RequestBody ServiceType serviceType) {
        return repository.save(serviceType);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceType> update(@PathVariable Long id, @RequestBody ServiceType details) {
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
