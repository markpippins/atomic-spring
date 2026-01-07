package com.angrysurfer.atomic.hostserver.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angrysurfer.atomic.hostserver.client.ServicesConsoleClient;
import com.angrysurfer.atomic.hostserver.entity.Host;
import com.angrysurfer.atomic.hostserver.repository.HostRepository;

@RestController
@RequestMapping("/api/servers")
@CrossOrigin(origins = "*")
public class HostController {

    private static final Logger log = LoggerFactory.getLogger(HostController.class);
    private final ServicesConsoleClient client;
    private final HostRepository hostRepository;

    public HostController(ServicesConsoleClient client, HostRepository hostRepository) {
        this.client = client;
        this.hostRepository = hostRepository;
    }

    @GetMapping
    public List<Host> getAllServers() {
        log.info("Fetching all hosts/servers from database");
        return hostRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Host> getServerById(@PathVariable Long id) {
        log.info("Fetching server by ID: {}", id);
        return hostRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/hostname/{hostname}")
    public ResponseEntity<Host> getServerByHostname(@PathVariable String hostname) {
        log.info("Fetching server by hostname: {}", hostname);
        return hostRepository.findByHostname(hostname)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}