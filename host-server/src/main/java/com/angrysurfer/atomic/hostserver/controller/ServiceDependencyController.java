package com.angrysurfer.atomic.hostserver.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.angrysurfer.atomic.hostserver.entity.Service;
import com.angrysurfer.atomic.hostserver.repository.ServiceRepository;

@RestController
@RequestMapping("/api/dependencies")
@CrossOrigin(origins = "*")
public class ServiceDependencyController {

    private static final Logger log = LoggerFactory.getLogger(ServiceDependencyController.class);

    @Autowired
    private ServiceRepository serviceRepository;

    @GetMapping
    public List<Map<String, Object>> getAllDependencies() {
        log.info("Fetching all service dependencies");
        List<Map<String, Object>> edges = new ArrayList<>();
        List<Service> services = serviceRepository.findAll();

        for (Service source : services) {
            // Note: getDependencies() might be lazy-loaded.
            // Since we are in Controller scope with OpenSessionInView (default in Boot),
            // this should work.
            if (source.getDependencies() != null) {
                for (Service target : source.getDependencies()) {
                    Map<String, Object> edge = new HashMap<>();
                    edge.put("sourceServiceId", source.getId());
                    edge.put("targetServiceId", target.getId());
                    edge.put("type", "static");
                    edges.add(edge);
                }
            }
        }

        log.debug("Found {} dependency edges", edges.size());
        return edges;
    }
}
