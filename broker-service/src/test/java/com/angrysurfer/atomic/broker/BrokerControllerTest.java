package com.angrysurfer.atomic.broker;

import com.angrysurfer.atomic.broker.api.ServiceRequest;
import com.angrysurfer.atomic.broker.api.ServiceResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrokerControllerTest {

    @Mock
    private Broker broker;

    private BrokerController brokerController;

    @BeforeEach
    void setUp() {
        brokerController = new BrokerController(broker);
    }

    @Test
    void testSubmitRequestSuccess() {
        // Arrange
        ServiceRequest request = new ServiceRequest("testService", "testOperation", 
            Collections.emptyMap(), "test-request");
        ServiceResponse<String> mockResponse = ServiceResponse.ok("Test Data", "test-request");

        when(broker.submit(any(ServiceRequest.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = brokerController.submitRequest(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ServiceResponse);
        ServiceResponse<?> responseEntity = (ServiceResponse<?>) response.getBody();
        assertTrue(responseEntity.isOk());
        assertEquals("Test Data", responseEntity.getData());
        assertEquals("test-request", responseEntity.getRequestId());
        
        verify(broker).submit(any(ServiceRequest.class));
    }

    @Test
    void testSubmitRequestError() {
        // Arrange
        ServiceRequest request = new ServiceRequest("testService", "testOperation", 
            Collections.emptyMap(), "test-request");
        ServiceResponse<String> mockResponse = ServiceResponse.error(
            java.util.List.of(java.util.Map.of("error", "Service error")), "test-request");

        when(broker.submit(any(ServiceRequest.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = brokerController.submitRequest(request);

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ServiceResponse);
        ServiceResponse<?> responseEntity = (ServiceResponse<?>) response.getBody();
        assertFalse(responseEntity.isOk());
        assertNotNull(responseEntity.getErrors());
        
        verify(broker).submit(any(ServiceRequest.class));
    }

    @Test
    void testTestBrokerEndpoint() {
        // Arrange
        ServiceResponse<String> mockResponse = ServiceResponse.ok("Test Data", "test-request");

        when(broker.submit(any(ServiceRequest.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = brokerController.testBroker();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ServiceResponse);
        ServiceResponse<?> responseEntity = (ServiceResponse<?>) response.getBody();
        assertTrue(responseEntity.isOk());
        assertEquals("Test Data", responseEntity.getData());
        assertEquals("test-request", responseEntity.getRequestId());
        
        verify(broker).submit(any(ServiceRequest.class));
    }

    @Test
    void testTestBrokerEndpointError() {
        // Arrange
        ServiceResponse<String> mockResponse = ServiceResponse.error(
            java.util.List.of(java.util.Map.of("error", "Test error")), "test-request");

        when(broker.submit(any(ServiceRequest.class))).thenReturn(mockResponse);

        // Act
        ResponseEntity<?> response = brokerController.testBroker();

        // Assert
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ServiceResponse);
        ServiceResponse<?> responseEntity = (ServiceResponse<?>) response.getBody();
        assertFalse(responseEntity.isOk());
        assertNotNull(responseEntity.getErrors());
        
        verify(broker).submit(any(ServiceRequest.class));
    }

    @Test
    void testBrokerSubmitInvokedWithCorrectRequest() {
        // Arrange
        ServiceRequest request = new ServiceRequest("userService", "createUser", 
            java.util.Map.of("name", "John", "email", "john@example.com"), "request-123");
        
        ServiceResponse<String> mockResponse = ServiceResponse.ok("User created", "request-123");

        // Act
        ResponseEntity<?> response = brokerController.submitRequest(request);

        // Verify that broker.submit was called with the exact same request
        verify(broker).submit(eq(request));
    }

    @Test
    void testNullRequestToSubmitRequest() {
        // Arrange
        ServiceRequest request = null;
        
        // This should still be handled by the broker which will validate it
        when(broker.submit(any(ServiceRequest.class))).thenReturn(
            ServiceResponse.error(java.util.List.of(java.util.Map.of("error", "Invalid request")), "error"));

        // Act
        ResponseEntity<?> response = brokerController.submitRequest(request);

        // Assert
        // The response depends on how the broker handles a null request
        verify(broker).submit(isNull(ServiceRequest.class));
    }

    @Test
    void testBrokerControllerInitialization() {
        // Test that the controller is properly initialized with the broker
        assertNotNull(brokerController);
        
        // Check that the broker field is set
        // Note: We can't directly access private fields, so we test behavior instead
        ServiceRequest request = new ServiceRequest("test", "test", Collections.emptyMap(), "test");
        ServiceResponse<String> expectedResponse = ServiceResponse.ok("test", "test");
        when(broker.submit(any(ServiceRequest.class))).thenReturn(expectedResponse);
        
        ResponseEntity<?> response = brokerController.submitRequest(request);
        
        assertNotNull(response);
        verify(broker).submit(any(ServiceRequest.class));
    }
}