package com.angrysurfer.atomic.broker.gateway.service;

import com.angrysurfer.atomic.broker.gateway.service.ServiceDiscoveryClient.ServiceDetails;
import com.angrysurfer.atomic.broker.gateway.service.ServiceDiscoveryClient.ServiceInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceDiscoveryClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ServiceDiscoveryClient discoveryClient;

    @BeforeEach
    void setUp() {
        discoveryClient = new ServiceDiscoveryClient();
        discoveryClient.setRestTemplate(restTemplate);
        discoveryClient.setHostServerUrl("http://localhost:8085");
    }

    @Test
    void findServiceByOperation_WithExistingService_ShouldReturnServiceInfo() {
        // Given
        String operation = "testOperation";
        ServiceInfo expectedServiceInfo = new ServiceInfo();
        expectedServiceInfo.setName("testService");
        expectedServiceInfo.setId(1L);

        when(restTemplate.getForObject(
                eq("http://localhost:8085/api/registry/services/by-operation/testOperation"),
                eq(ServiceInfo.class)
        )).thenReturn(expectedServiceInfo);

        // When
        Optional<ServiceInfo> result = discoveryClient.findServiceByOperation(operation);

        // Then
        assertTrue(result.isPresent());
        assertEquals("testService", result.get().getName());
        assertEquals(Long.valueOf(1L), result.get().getId());
        verify(restTemplate).getForObject(
                eq("http://localhost:8085/api/registry/services/by-operation/testOperation"),
                eq(ServiceInfo.class)
        );
    }

    @Test
    void findServiceByOperation_WithNoServiceFound_ShouldReturnEmpty() {
        // Given
        String operation = "nonExistentOperation";

        when(restTemplate.getForObject(
                eq("http://localhost:8085/api/registry/services/by-operation/nonExistentOperation"),
                eq(ServiceInfo.class)
        )).thenReturn(null);

        // When
        Optional<ServiceInfo> result = discoveryClient.findServiceByOperation(operation);

        // Then
        assertFalse(result.isPresent());
        verify(restTemplate).getForObject(
                eq("http://localhost:8085/api/registry/services/by-operation/nonExistentOperation"),
                eq(ServiceInfo.class)
        );
    }

    @Test
    void findServiceByOperation_WithRestTemplateException_ShouldReturnEmpty() {
        // Given
        String operation = "errorOperation";

        when(restTemplate.getForObject(
                eq("http://localhost:8085/api/registry/services/by-operation/errorOperation"),
                eq(ServiceInfo.class)
        )).thenThrow(new RuntimeException("Connection failed"));

        // When
        Optional<ServiceInfo> result = discoveryClient.findServiceByOperation(operation);

        // Then
        assertFalse(result.isPresent());
        verify(restTemplate).getForObject(
                eq("http://localhost:8085/api/registry/services/by-operation/errorOperation"),
                eq(ServiceInfo.class)
        );
    }

    @Test
    void getServiceDetails_WithExistingService_ShouldReturnServiceDetails() {
        // Given
        String serviceName = "testService";
        ServiceDetails expectedServiceDetails = new ServiceDetails();
        expectedServiceDetails.setServiceName("testService");
        expectedServiceDetails.setEndpoint("http://test-service:8080");

        when(restTemplate.getForObject(
                eq("http://localhost:8085/api/registry/services/testService/details"),
                eq(ServiceDetails.class)
        )).thenReturn(expectedServiceDetails);

        // When
        Optional<ServiceDetails> result = discoveryClient.getServiceDetails(serviceName);

        // Then
        assertTrue(result.isPresent());
        assertEquals("testService", result.get().getServiceName());
        assertEquals("http://test-service:8080", result.get().getEndpoint());
        verify(restTemplate).getForObject(
                eq("http://localhost:8085/api/registry/services/testService/details"),
                eq(ServiceDetails.class)
        );
    }

    @Test
    void getServiceDetails_WithNoServiceFound_ShouldReturnEmpty() {
        // Given
        String serviceName = "nonExistentService";

        when(restTemplate.getForObject(
                eq("http://localhost:8085/api/registry/services/nonExistentService/details"),
                eq(ServiceDetails.class)
        )).thenReturn(null);

        // When
        Optional<ServiceDetails> result = discoveryClient.getServiceDetails(serviceName);

        // Then
        assertFalse(result.isPresent());
        verify(restTemplate).getForObject(
                eq("http://localhost:8085/api/registry/services/nonExistentService/details"),
                eq(ServiceDetails.class)
        );
    }

    @Test
    void getServiceDetails_WithRestTemplateException_ShouldReturnEmpty() {
        // Given
        String serviceName = "errorService";

        when(restTemplate.getForObject(
                eq("http://localhost:8085/api/registry/services/errorService/details"),
                eq(ServiceDetails.class)
        )).thenThrow(new RuntimeException("Connection failed"));

        // When
        Optional<ServiceDetails> result = discoveryClient.getServiceDetails(serviceName);

        // Then
        assertFalse(result.isPresent());
        verify(restTemplate).getForObject(
                eq("http://localhost:8085/api/registry/services/errorService/details"),
                eq(ServiceDetails.class)
        );
    }

    @Test
    void serviceInfo_GettersAndSetters_ShouldWorkCorrectly() {
        // Given
        ServiceInfo serviceInfo = new ServiceInfo();

        // When
        serviceInfo.setId(123L);
        serviceInfo.setName("testService");
        serviceInfo.setDescription("Test service description");
        serviceInfo.setStatus("ACTIVE");

        // Then
        assertEquals(Long.valueOf(123L), serviceInfo.getId());
        assertEquals("testService", serviceInfo.getName());
        assertEquals("Test service description", serviceInfo.getDescription());
        assertEquals("ACTIVE", serviceInfo.getStatus());
    }

    @Test
    void serviceDetails_GettersAndSetters_ShouldWorkCorrectly() {
        // Given
        ServiceDetails serviceDetails = new ServiceDetails();

        // When
        serviceDetails.setServiceName("testService");
        serviceDetails.setEndpoint("http://test-service:8080");
        serviceDetails.setHealthCheck("/health");
        serviceDetails.setFramework("Spring Boot");
        serviceDetails.setStatus("ACTIVE");
        serviceDetails.setOperations("op1,op2,op3");

        // Then
        assertEquals("testService", serviceDetails.getServiceName());
        assertEquals("http://test-service:8080", serviceDetails.getEndpoint());
        assertEquals("/health", serviceDetails.getHealthCheck());
        assertEquals("Spring Boot", serviceDetails.getFramework());
        assertEquals("ACTIVE", serviceDetails.getStatus());
        assertEquals("op1,op2,op3", serviceDetails.getOperations());
    }
}