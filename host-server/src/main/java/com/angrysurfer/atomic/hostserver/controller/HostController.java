package com.angrysurfer.atomic.hostserver.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angrysurfer.atomic.hostserver.entity.Host;
import com.angrysurfer.atomic.hostserver.repository.HostRepository;

@RestController
@RequestMapping("/api/servers")
@CrossOrigin(origins = "*")
@SuppressWarnings("null")
public class HostController {

    private static final Logger log = LoggerFactory.getLogger(HostController.class);

    @Autowired
    private HostRepository hostRepository;

    @GetMapping
    public List<Host> getAllServers() {
        log.info("Fetching all hosts/servers");
        List<Host> hosts = hostRepository.findAll();
        log.debug("Fetched {} hosts", hosts.size());
        return hosts;
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Host> getServerById(@PathVariable Long id) {
        log.info("Fetching server by ID: {}", id);
        return hostRepository.findById(id)
            .map(host -> {
                log.debug("Server found with ID: {}", id);
                return ResponseEntity.ok(host);
            })
            .orElseGet(() -> {
                log.warn("Server not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            });
    }

    @GetMapping("/hostname/{hostname}")
    public ResponseEntity<Host> getServerByHostname(@PathVariable String hostname) {
        log.info("Fetching server by hostname: {}", hostname);
        return hostRepository.findByHostname(hostname)
            .map(host -> {
                log.debug("Server found with hostname: {}", hostname);
                return ResponseEntity.ok(host);
            })
            .orElseGet(() -> {
                log.warn("Server not found with hostname: {}", hostname);
                return ResponseEntity.notFound().build();
            });
    }

    @GetMapping("/environment/{environment}")
    public List<Host> getServersByEnvironment(@PathVariable Host.ServerEnvironment environment) {
        log.info("Fetching servers by environment: {}", environment);
        List<Host> hosts = hostRepository.findByEnvironment(environment);
        log.debug("Fetched {} servers for environment: {}", hosts.size(), environment);
        return hosts;
    }

    @GetMapping("/status/{status}")
    public List<Host> getServersByStatus(@PathVariable Host.ServerStatus status) {
        log.info("Fetching servers by status: {}", status);
        List<Host> hosts = hostRepository.findByStatus(status);
        log.debug("Fetched {} servers with status: {}", hosts.size(), status);
        return hosts;
    }

    @Autowired
    private com.angrysurfer.atomic.hostserver.repository.ServerTypeRepository serverTypeRepository;

    @GetMapping("/type/{typeId}")
    public List<Host> getServersByType(@PathVariable Long typeId) {
        log.info("Fetching servers by type ID: {}", typeId);
        return serverTypeRepository.findById(typeId)
                .map(type -> {
                    List<Host> hosts = hostRepository.findByType(type);
                    log.debug("Fetched {} servers for type ID: {}", hosts.size(), typeId);
                    return hosts;
                })
                .orElse(List.of());
    }

    @PostMapping
    public ResponseEntity<Host> createServer(@RequestBody Host host) {
        log.info("Creating server with hostname: {}", host.getHostname());
        try {
            Host savedHost = hostRepository.save(host);
            log.info("Server created successfully with ID: {}", savedHost.getId());
            return new ResponseEntity<>(savedHost, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating server: {}", host.getHostname(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Host> updateServer(@PathVariable Long id, @RequestBody Host hostDetails) {
        log.info("Updating server with ID: {}", id);
        return hostRepository.findById(id)
            .map(host -> {
                host.setHostname(hostDetails.getHostname());
                host.setIpAddress(hostDetails.getIpAddress());
                host.setType(hostDetails.getType());
                host.setEnvironment(hostDetails.getEnvironment());
                host.setOperatingSystem(hostDetails.getOperatingSystem());
                host.setCpuCores(hostDetails.getCpuCores());
                host.setMemoryMb(hostDetails.getMemoryMb());
                host.setDiskGb(hostDetails.getDiskGb());
                host.setRegion(hostDetails.getRegion());
                host.setCloudProvider(hostDetails.getCloudProvider());
                host.setStatus(hostDetails.getStatus());
                host.setDescription(hostDetails.getDescription());
                Host updatedHost = hostRepository.save(host);
                log.info("Server updated successfully with ID: {}", updatedHost.getId());
                return ResponseEntity.ok(updatedHost);
            })
            .orElseGet(() -> {
                log.warn("Server not found with ID: {} for update", id);
                return ResponseEntity.notFound().build();
            });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServer(@PathVariable Long id) {
        log.info("Deleting server with ID: {}", id);
        return hostRepository.findById(id)
            .map(host -> {
                hostRepository.delete(host);
                log.info("Server deleted successfully with ID: {}", id);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElseGet(() -> {
                log.warn("Server not found with ID: {} for deletion", id);
                return ResponseEntity.notFound().build();
            });
    }
}