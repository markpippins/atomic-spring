package com.angrysurfer.atomic.broker.gateway.service;

import com.angrysurfer.atomic.broker.gateway.service.ServiceDiscoveryClient.ServiceDetails;
import com.angrysurfer.atomic.broker.gateway.service.ServiceDiscoveryClient.ServiceInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalServiceInvokerTest {

    @Mock
    private ServiceDiscoveryClient discoveryClient;

    @Mock
    private RestTemplate restTemplate;

    private ExternalServiceInvoker serviceInvoker;

    @BeforeEach
    void setUp() {
        serviceInvoker = new ExternalServiceInvoker();
        serviceInvoker.setDiscoveryClient(discoveryClient);
        serviceInvoker.setRestTemplate(restTemplate);
    }

    @Test
    void invokeOperation_WithValidServiceAndOperation_ShouldReturnSuccess() {
        // Given
        String operation = "testOperation";
        Object requestBody = new Object();
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setName("testService");
        ServiceDetails serviceDetails = new ServiceDetails();
        serviceDetails.setEndpoint("http://test-service:8080");

        when(discoveryClient.findServiceByOperation(operation)).thenReturn(Optional.of(serviceInfo));
        when(discoveryClient.getServiceDetails("testService")).thenReturn(Optional.of(serviceDetails));

        ResponseEntity<String> mockResponse = ResponseEntity.ok("success");
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(mockResponse);

        // When
        ResponseEntity<String> result = serviceInvoker.invokeOperation(operation, requestBody);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("success", result.getBody());

        // Verify that the correct URL was called
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(
                urlCaptor.capture(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        );
        assertEquals("http://test-service:8080/testOperation", urlCaptor.getValue());
    }

    @Test
    void invokeOperation_WithNoServiceFound_ShouldReturnNotFound() {
        // Given
        String operation = "nonExistentOperation";
        Object requestBody = new Object();

        when(discoveryClient.findServiceByOperation(operation)).thenReturn(Optional.empty());

        // When
        ResponseEntity<String> result = serviceInvoker.invokeOperation(operation, requestBody);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(restTemplate, never()).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    void invokeOperation_WithNoServiceDetails_ShouldReturnInternalServerError() {
        // Given
        String operation = "testOperation";
        Object requestBody = new Object();
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setName("testService");

        when(discoveryClient.findServiceByOperation(operation)).thenReturn(Optional.of(serviceInfo));
        when(discoveryClient.getServiceDetails("testService")).thenReturn(Optional.empty());

        // When
        ResponseEntity<String> result = serviceInvoker.invokeOperation(operation, requestBody);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        verify(restTemplate, never()).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    void invokeOperation_WithRestTemplateException_ShouldReturnInternalServerError() {
        // Given
        String operation = "testOperation";
        Object requestBody = new Object();
        ServiceInfo serviceInfo = new ServiceInfo();
        serviceInfo.setName("testService");
        ServiceDetails serviceDetails = new ServiceDetails();
        serviceDetails.setEndpoint("http://test-service:8080");

        when(discoveryClient.findServiceByOperation(operation)).thenReturn(Optional.of(serviceInfo));
        when(discoveryClient.getServiceDetails("testService")).thenReturn(Optional.of(serviceDetails));
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenThrow(new RuntimeException("Connection failed"));

        // When
        ResponseEntity<String> result = serviceInvoker.invokeOperation(operation, requestBody);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertTrue(result.getBody().contains("Failed to invoke external service"));
    }

    @Test
    void healthCheck_WithHealthyService_ShouldReturnTrue() {
        // Given
        String serviceName = "testService";
        ServiceDetails serviceDetails = new ServiceDetails();
        serviceDetails.setEndpoint("http://test-service:8080");

        when(discoveryClient.getServiceDetails(serviceName)).thenReturn(Optional.of(serviceDetails));
        when(restTemplate.getForEntity("http://test-service:8080", String.class))
                .thenReturn(ResponseEntity.ok("healthy"));

        // When
        boolean result = serviceInvoker.healthCheck(serviceName);

        // Then
        assertTrue(result);
    }

    @Test
    void healthCheck_WithUnhealthyService_ShouldReturnFalse() {
        // Given
        String serviceName = "testService";
        ServiceDetails serviceDetails = new ServiceDetails();
        serviceDetails.setEndpoint("http://test-service:8080");

        when(discoveryClient.getServiceDetails(serviceName)).thenReturn(Optional.of(serviceDetails));
        when(restTemplate.getForEntity("http://test-service:8080", String.class))
                .thenReturn(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());

        // When
        boolean result = serviceInvoker.healthCheck(serviceName);

        // Then
        assertFalse(result);
    }

    @Test
    void healthCheck_WithServiceNotFoundException_ShouldReturnFalse() {
        // Given
        String serviceName = "nonExistentService";

        when(discoveryClient.getServiceDetails(serviceName)).thenReturn(Optional.empty());

        // When
        boolean result = serviceInvoker.healthCheck(serviceName);

        // Then
        assertFalse(result);
    }

    @Test
    void healthCheck_WithRestTemplateException_ShouldReturnFalse() {
        // Given
        String serviceName = "testService";
        ServiceDetails serviceDetails = new ServiceDetails();
        serviceDetails.setEndpoint("http://test-service:8080");

        when(discoveryClient.getServiceDetails(serviceName)).thenReturn(Optional.of(serviceDetails));
        when(restTemplate.getForEntity("http://test-service:8080", String.class))
                .thenThrow(new RuntimeException("Connection failed"));

        // When
        boolean result = serviceInvoker.healthCheck(serviceName);

        // Then
        assertFalse(result);
    }
}