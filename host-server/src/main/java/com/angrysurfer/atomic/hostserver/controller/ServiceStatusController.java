package com.angrysurfer.atomic.hostserver.controller;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.angrysurfer.atomic.hostserver.dto.ServiceStatus;
import com.angrysurfer.atomic.hostserver.service.ServiceStatusCacheService;

import lombok.RequiredArgsConstructor;

/**
 * REST controller for real-time service status data.
 * Provides fast, cached data for the 3D visualizer.
 */
@RestController
@RequestMapping("/api/status")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ServiceStatusController {

    private static final Logger log = LoggerFactory.getLogger(ServiceStatusController.class);

    private final ServiceStatusCacheService cacheService;

    /**
     * Get all service statuses (cached from Redis for fast response)
     */
    @GetMapping
    public ResponseEntity<List<ServiceStatus>> getAllStatuses() {
        List<ServiceStatus> statuses = cacheService.getAllServiceStatuses();
        return ResponseEntity.ok(statuses);
    }

    /**
     * Get status for a specific service
     */
    @GetMapping("/{serviceName}")
    public ResponseEntity<ServiceStatus> getServiceStatus(@PathVariable String serviceName) {
        return cacheService.getServiceStatus(serviceName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get last heartbeat time for a service
     */
    @GetMapping("/{serviceName}/heartbeat")
    public ResponseEntity<Map<String, Object>> getLastHeartbeat(@PathVariable String serviceName) {
        Optional<Instant> lastHeartbeat = cacheService.getLastHeartbeat(serviceName);

        if (lastHeartbeat.isPresent()) {
            return ResponseEntity.ok(Map.of(
                    "serviceName", serviceName,
                    "lastHeartbeat", lastHeartbeat.get().toString(),
                    "isStale", cacheService.isServiceStale(serviceName)));
        }

        return ResponseEntity.notFound().build();
    }

    /**
     * Get metrics for a specific service
     */
    @GetMapping("/{serviceName}/metrics")
    public ResponseEntity<Map<String, Object>> getServiceMetrics(@PathVariable String serviceName) {
        return cacheService.getMetrics(serviceName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Post metrics from a service
     */
    @PostMapping("/{serviceName}/metrics")
    public ResponseEntity<Map<String, String>> postServiceMetrics(
            @PathVariable String serviceName,
            @RequestBody Map<String, Object> metrics) {
        cacheService.storeMetrics(serviceName, metrics);
        return ResponseEntity.ok(Map.of(
                "message", "Metrics stored",
                "serviceName", serviceName));
    }

    /**
     * Get Redis health status
     */
    @GetMapping("/health/redis")
    public ResponseEntity<Map<String, Object>> getRedisHealth() {
        boolean healthy = cacheService.isRedisHealthy();
        return ResponseEntity.ok(Map.of(
                "redisAvailable", healthy,
                "timestamp", Instant.now().toString()));
    }

    /**
     * Server-Sent Events endpoint for real-time updates.
     * The 3D visualizer can subscribe to this for live updates.
     */
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamStatusUpdates() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // TODO: Subscribe to Redis pub/sub and forward events to SSE
        // For now, return an emitter that will be managed by a listener

        log.info("New SSE client connected for status updates");

        emitter.onCompletion(() -> log.debug("SSE client disconnected"));
        emitter.onTimeout(() -> log.debug("SSE connection timed out"));
        emitter.onError(e -> log.warn("SSE error", e));

        return emitter;
    }
}
