package com.angrysurfer.atomic.broker.gateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.angrysurfer.atomic.broker.api.ServiceRegistration;
import com.angrysurfer.atomic.registry.service.RegistryService;

@Component
public class HostServerRegistrationService {

    private static final Logger log = LoggerFactory.getLogger(HostServerRegistrationService.class);

    @Value("${host.server.url:http://localhost:8085}")
    private String hostServerUrl;

    @Value("${server.port:8080}")
    private int port;

    @Value("${spring.application.name:spring-broker-gateway}")
    private String serviceName;

    @Value("${service.host:localhost}")
    private String serviceHost;

    @Value("${host.server.registration.enabled:true}")
    private boolean registrationEnabled;

    @Value("${host.server.heartbeat.interval.seconds:30}")
    private int heartbeatInterval;

    private ScheduledExecutorService scheduler;
    private final RestTemplate restTemplate;
    private final RegistryService registryService;

    public HostServerRegistrationService(
            @Qualifier("gatewayRestTemplate") RestTemplate restTemplate,
            RegistryService registryService) {
        this.restTemplate = restTemplate;
        this.registryService = registryService;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Order(100) // Run after BrokerAutoRegistration populates the in-memory registry
    public void onStart() {
        if (!registrationEnabled) {
            log.info("Host-server registration is disabled");
            return;
        }

        log.info("Starting host-server registration service");

        registerService();

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(
                this::sendHeartbeat,
                heartbeatInterval,
                heartbeatInterval,
                TimeUnit.SECONDS);

        log.info("Host-server registration service started. Heartbeat interval: {}s", heartbeatInterval);
    }

    @EventListener(ContextClosedEvent.class)
    public void onStop() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
        log.info("Host-server registration service stopped");
    }

    private void registerService() {
        Map<String, Object> registration = new HashMap<>();
        registration.put("serviceName", serviceName);
        registration.put("operations", List.of(
                "submitRequest",
                "routeRequest",
                "healthCheck"));
        registration.put("endpoint", String.format("http://%s:%d", serviceHost, port));
        registration.put("healthCheck", String.format("http://%s:%d/api/health", serviceHost, port));
        registration.put("framework", "Spring Boot");
        registration.put("version", "3.2.x");
        registration.put("port", port);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", "spring-broker-gateway");
        metadata.put("language", "Java");
        metadata.put("runtime", "Spring Boot");
        registration.put("metadata", metadata);

        List<Map<String, Object>> hostedServices = getHostedServices();
        if (!hostedServices.isEmpty()) {
            registration.put("hostedServices", hostedServices);
            log.info("Registering {} hosted services with host-server", hostedServices.size());
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(registration, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    hostServerUrl + "/api/registry/register",
                    request,
                    Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Successfully registered with host-server at {}", hostServerUrl);
                log.info("Service: {} on port {}", serviceName, port);
            } else {
                log.warn("Failed to register with host-server. Status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error registering with host-server: {}", e.getMessage());
        }
    }

    private List<Map<String, Object>> getHostedServices() {
        List<Map<String, Object>> hostedServices = new ArrayList<>();
        try {
            List<ServiceRegistration> registrations = registryService.getAllServices();
            log.debug("Found {} services in local registry", registrations.size());
            for (ServiceRegistration reg : registrations) {
                Map<String, Object> serviceInfo = new HashMap<>();
                serviceInfo.put("serviceName", reg.getServiceName());
                serviceInfo.put("operations", reg.getOperations());
                serviceInfo.put("framework", "Spring Boot");
                serviceInfo.put("status", reg.getStatus() != null ? reg.getStatus().name() : "HEALTHY");
                serviceInfo.put("type", "embedded");
                serviceInfo.put("endpoint", reg.getEndpoint());
                serviceInfo.put("healthCheck", reg.getHealthCheck());
                hostedServices.add(serviceInfo);
                log.debug("Added hosted service: {} with {} operations",
                        reg.getServiceName(), reg.getOperations().size());
            }
        } catch (Exception e) {
            log.warn("Could not retrieve hosted services: {}", e.getMessage());
        }
        return hostedServices;
    }

    private void sendHeartbeat() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>("{}", headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    hostServerUrl + "/api/registry/heartbeat/" + serviceName,
                    request,
                    Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("Heartbeat sent successfully");
            } else {
                log.warn("Heartbeat failed. Status: {}", response.getStatusCode());
            }
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            log.warn("Service not found on host-server, re-registering...");
            registerService();
        } catch (Exception e) {
            log.error("Error sending heartbeat: {}", e.getMessage());
        }
    }
}
