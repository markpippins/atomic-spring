package com.angrysurfer.atomic.hostserver.controller;

import java.util.List;

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
public class HostController {
    
    @Autowired
    private HostRepository hostRepository;
    
    @GetMapping
    public List<Host> getAllServers() {
        return hostRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Host> getServerById(@PathVariable Long id) {
        return hostRepository.findById(id)
            .map(host -> ResponseEntity.ok(host))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/hostname/{hostname}")
    public ResponseEntity<Host> getServerByHostname(@PathVariable String hostname) {
        return hostRepository.findByHostname(hostname)
            .map(host -> ResponseEntity.ok(host))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/environment/{environment}")
    public List<Host> getServersByEnvironment(@PathVariable Host.ServerEnvironment environment) {
        return hostRepository.findByEnvironment(environment);
    }
    
    @GetMapping("/status/{status}")
    public List<Host> getServersByStatus(@PathVariable Host.ServerStatus status) {
        return hostRepository.findByStatus(status);
    }
    
    @GetMapping("/type/{type}")
    public List<Host> getServersByType(@PathVariable Host.ServerType type) {
        return hostRepository.findByType(type);
    }
    
    @PostMapping
    public ResponseEntity<Host> createServer(@RequestBody Host host) {
        Host savedHost = hostRepository.save(host);
        return new ResponseEntity<>(savedHost, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Host> updateServer(@PathVariable Long id, @RequestBody Host hostDetails) {
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
                return ResponseEntity.ok(hostRepository.save(host));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServer(@PathVariable Long id) {
        return hostRepository.findById(id)
            .map(host -> {
                hostRepository.delete(host);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }
}