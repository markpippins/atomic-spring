package com.angrysurfer.atomic.search.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/search")
@CrossOrigin(origins = "*")
public class SearchController {

    @Value("${server.port:8084}")
    private String serverPort;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "search-service");
        response.put("port", serverPort);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/perform")
    public ResponseEntity<Map<String, Object>> performSearch(@RequestParam String query) {
        Map<String, Object> response = new HashMap<>();
        response.put("query", query);
        response.put("results", new String[]{"Sample result 1", "Sample result 2", "Sample result 3"});
        response.put("service", "search-service");
        response.put("port", serverPort);
        return ResponseEntity.ok(response);
    }
}