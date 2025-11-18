package com.angrysurfer.atomic.broker;

import com.angrysurfer.atomic.broker.api.ServiceRequest;
import com.angrysurfer.atomic.broker.api.ServiceResponse;
import com.angrysurfer.atomic.broker.spi.BrokerOperation;
import com.angrysurfer.atomic.broker.spi.BrokerParam;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrokerTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    private Validator validator;

    private Broker broker;

    @BeforeEach
    void setUp() {
        // Initialize validator using Spring's implementation
        LocalValidatorFactoryBean localValidatorFactoryBean = new LocalValidatorFactoryBean();
        localValidatorFactoryBean.afterPropertiesSet();
        validator = localValidatorFactoryBean;

        broker = new Broker(applicationContext, objectMapper, validator);
    }

    @Test
    void testSubmitSuccess() {
        // Arrange
        TestBean testBean = new TestBean();
        ServiceRequest request = new ServiceRequest("testBean", "testOperation", 
            Map.of("param1", "value1"), "test-request");

        when(applicationContext.containsBean("testBean")).thenReturn(true);
        when(applicationContext.getBean("testBean")).thenReturn(testBean);
        when(objectMapper.convertValue(any(), any(com.fasterxml.jackson.core.type.TypeReference.class))).thenReturn("value1");

        // Act
        ServiceResponse<?> response = broker.submit(request);

        // Assert
        assertTrue(response.isOk());
        assertEquals("test-request", response.getRequestId());
        assertEquals("Test Result", response.getData());
    }

    @Test
    void testSubmitWithServiceResponseReturn() {
        // Arrange
        ServiceResponseReturnBean serviceResponseReturnBean = new ServiceResponseReturnBean();
        ServiceRequest request = new ServiceRequest("serviceResponseReturnBean", "getServiceResponse", 
            new HashMap<>(), "test-request");

        when(applicationContext.containsBean("serviceResponseReturnBean")).thenReturn(true);
        when(applicationContext.getBean("serviceResponseReturnBean")).thenReturn(serviceResponseReturnBean);

        // Act
        ServiceResponse<?> response = broker.submit(request);

        // Assert
        assertTrue(response.isOk());
        assertEquals("test-request", response.getRequestId());
        assertEquals("Direct ServiceResponse", response.getData());
    }

    @Test
    void testSubmitNoSuchBean() {
        // Arrange
        ServiceRequest request = new ServiceRequest("nonExistentBean", "testOperation", 
            new HashMap<>(), "test-request");

        when(applicationContext.containsBean("nonExistentBean")).thenReturn(false);
        when(applicationContext.getBeanDefinitionNames()).thenReturn(new String[0]);

        // Act
        ServiceResponse<?> response = broker.submit(request);

        // Assert
        assertFalse(response.isOk());
        assertNotNull(response.getErrors());
        assertTrue(response.getErrors().stream()
            .anyMatch(error -> error.get("message").toString().contains("Service bean not found")));
    }

    @Test
    void testSubmitNoSuchMethod() {
        // Arrange
        TestBean testBean = new TestBean();
        ServiceRequest request = new ServiceRequest("testBean", "nonExistentOperation", 
            new HashMap<>(), "test-request");

        when(applicationContext.containsBean("testBean")).thenReturn(true);
        when(applicationContext.getBean("testBean")).thenReturn(testBean);

        // Act
        ServiceResponse<?> response = broker.submit(request);

        // Assert
        assertFalse(response.isOk());
        assertNotNull(response.getErrors());
        assertTrue(response.getErrors().stream()
            .anyMatch(error -> error.get("message").toString().contains("Operation not exposed")));
    }

    @Test
    void testSubmitMissingServiceName() {
        // Arrange
        ServiceRequest request = new ServiceRequest(null, "testOperation", new HashMap<>(), "test-request");

        // Act
        ServiceResponse<?> response = broker.submit(request);

        // Assert
        assertFalse(response.isOk());
        assertNotNull(response.getErrors());
        assertTrue(response.getErrors().stream()
            .anyMatch(error -> error.get("message").toString().contains("Missing 'service' name")));
    }

    @Test
    void testSubmitMissingOperationName() {
        // Arrange
        TestBean testBean = new TestBean();
        ServiceRequest request = new ServiceRequest("testBean", null, new HashMap<>(), "test-request");

        when(applicationContext.containsBean("testBean")).thenReturn(true);
        when(applicationContext.getBean("testBean")).thenReturn(testBean);

        // Act
        ServiceResponse<?> response = broker.submit(request);

        // Assert
        assertFalse(response.isOk());
        assertNotNull(response.getErrors());
        assertTrue(response.getErrors().stream()
            .anyMatch(error -> error.get("message").toString().contains("Missing 'operation' name")));
    }

    @Test
    void testSubmitParameterBindingError() {
        // Arrange
        TestBean testBean = new TestBean();
        ServiceRequest request = new ServiceRequest("testBean", "testOperation", 
            Map.of("nonExistentParam", "value"), "test-request");

        when(applicationContext.containsBean("testBean")).thenReturn(true);
        when(applicationContext.getBean("testBean")).thenReturn(testBean);
        // Simulate conversion error
        when(objectMapper.convertValue(any(), any(com.fasterxml.jackson.core.type.TypeReference.class))).thenThrow(new IllegalArgumentException("Cannot convert"));

        // Act
        ServiceResponse<?> response = broker.submit(request);

        // Assert
        assertFalse(response.isOk());
        assertNotNull(response.getErrors());
        assertTrue(response.getErrors().stream()
            .anyMatch(error -> error.get("message").toString().contains("Cannot convert")));
    }

    @Test
    void testSubmitMethodInvocationError() {
        // Arrange
        ExceptionThrowingBean exceptionBean = new ExceptionThrowingBean();
        ServiceRequest request = new ServiceRequest("exceptionBean", "throwException", 
            new HashMap<>(), "test-request");

        when(applicationContext.containsBean("exceptionBean")).thenReturn(true);
        when(applicationContext.getBean("exceptionBean")).thenReturn(exceptionBean);

        // Act
        ServiceResponse<?> response = broker.submit(request);

        // Assert
        assertFalse(response.isOk());
        assertNotNull(response.getErrors());
        assertTrue(response.getErrors().stream()
            .anyMatch(error -> error.get("message").toString().contains("Test exception")));
    }

    @Test
    void testSubmitValidationErrors() {
        // Arrange
        ValidationBean validationBean = new ValidationBean();
        ServiceRequest request = new ServiceRequest("validationBean", "validateMethod", 
            Map.of("invalidValue", "a"), "test-request"); // "a" is less than min length of 3

        when(applicationContext.containsBean("validationBean")).thenReturn(true);
        when(applicationContext.getBean("validationBean")).thenReturn(validationBean);
        when(objectMapper.convertValue(any(), any(com.fasterxml.jackson.core.type.TypeReference.class))).thenReturn("a");

        // Act
        ServiceResponse<?> response = broker.submit(request);

        // Assert
        assertFalse(response.isOk());
        assertNotNull(response.getErrors());
    }

    @Test
    void testSubmitWithNullParams() {
        // Arrange
        TestBean testBean = new TestBean();
        ServiceRequest request = new ServiceRequest("testBean", "testOperation", null, "test-request");

        when(applicationContext.containsBean("testBean")).thenReturn(true);
        when(applicationContext.getBean("testBean")).thenReturn(testBean);
        when(objectMapper.convertValue(any(), any(com.fasterxml.jackson.core.type.TypeReference.class))).thenReturn(null);

        // Act
        ServiceResponse<?> response = broker.submit(request);

        // Assert
        assertTrue(response.isOk());
        // This should work because the method accepts a String param and null should be passed
    }

    @Test
    void testResolveBeanBySimpleName() {
        // Arrange
        TestBean testBean = new TestBean();
        ServiceRequest request = new ServiceRequest("TestBean", "testOperation", 
            Map.of("param1", "value1"), "test-request");

        when(applicationContext.containsBean("TestBean")).thenReturn(false);
        when(applicationContext.getBeanDefinitionNames()).thenReturn(new String[]{"testBeanInstance"});
        when(applicationContext.getBean("testBeanInstance")).thenReturn(testBean);
        when(objectMapper.convertValue(any(), any(com.fasterxml.jackson.core.type.TypeReference.class))).thenReturn("value1");

        // Act
        ServiceResponse<?> response = broker.submit(request);

        // Assert
        assertTrue(response.isOk());
        assertEquals("Test Result", response.getData());
    }

    // Test beans for mocking purposes
    public static class TestBean {
        @BrokerOperation("testOperation")
        public String testOperation(@BrokerParam("param1") String param1) {
            return "Test Result";
        }
    }

    public static class ServiceResponseReturnBean {
        @BrokerOperation("getServiceResponse")
        public ServiceResponse<String> getServiceResponse() {
            return ServiceResponse.ok("Direct ServiceResponse", "test-request");
        }
    }

    public static class ExceptionThrowingBean {
        @BrokerOperation("throwException")
        public String throwException() {
            throw new RuntimeException("Test exception");
        }
    }

    public static class ValidationBean {
        @BrokerOperation("validateMethod")
        public String validateMethod(@jakarta.validation.constraints.Size(min = 3) String invalidValue) {
            return "Valid";
        }
    }
}