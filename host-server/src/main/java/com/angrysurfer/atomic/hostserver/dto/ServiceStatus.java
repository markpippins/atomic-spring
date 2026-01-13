package com.angrysurfer.atomic.hostserver.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the real-time status of a service, stored in Redis.
 * This is ephemeral data that changes frequently (heartbeats, health, metrics).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Service unique identifier (from database) */
    private Long serviceId;

    /** Service name for quick lookup */
    private String serviceName;

    /** Current health status */
    private HealthState healthState;

    /** Last heartbeat timestamp */
    private Instant lastHeartbeat;

    /** Last health check timestamp */
    private Instant lastHealthCheck;

    /** Service endpoint URL */
    private String endpoint;

    /** Response time in milliseconds from last health check */
    private Long responseTimeMs;

    /** Number of active connections/requests */
    private Integer activeConnections;

    /** Custom metrics from the service */
    private Map<String, Object> metrics;

    /** Error message if unhealthy */
    private String errorMessage;

    /** Whether this status was published via pub/sub */
    private boolean published;

    public enum HealthState {
        HEALTHY,
        UNHEALTHY,
        DEGRADED,
        UNKNOWN,
        OFFLINE,
        STARTING,
        STOPPING
    }

    /**
     * Check if the service is considered stale (no heartbeat in threshold)
     */
    public boolean isStale(long thresholdSeconds) {
        if (lastHeartbeat == null) {
            return true;
        }
        return Instant.now().minusSeconds(thresholdSeconds).isAfter(lastHeartbeat);
    }

    /**
     * Create a status for a newly registered service
     */
    public static ServiceStatus createInitial(Long serviceId, String serviceName, String endpoint) {
        return ServiceStatus.builder()
                .serviceId(serviceId)
                .serviceName(serviceName)
                .endpoint(endpoint)
                .healthState(HealthState.UNKNOWN)
                .lastHeartbeat(Instant.now())
                .build();
    }

    /**
     * Create an offline status
     */
    public static ServiceStatus createOffline(Long serviceId, String serviceName) {
        return ServiceStatus.builder()
                .serviceId(serviceId)
                .serviceName(serviceName)
                .healthState(HealthState.OFFLINE)
                .lastHeartbeat(null)
                .build();
    }
}
