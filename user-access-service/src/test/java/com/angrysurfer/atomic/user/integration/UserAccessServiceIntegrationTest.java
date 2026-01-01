package com.angrysurfer.atomic.user.integration;

import com.angrysurfer.atomic.user.UserRegistrationDTO;
import com.angrysurfer.atomic.user.model.UserRegistration;
import com.angrysurfer.atomic.user.repository.UserRegistrationRepository;
import com.angrysurfer.atomic.user.service.UserAccessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
class UserAccessServiceIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:5.0")
            .withExposedPorts(27017);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private UserRegistrationRepository userRepository;

    @Autowired
    private UserAccessService userAccessService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void validateUser_WithValidUserInDatabase_ShouldReturnUserDto() {
        // Given
        UserRegistration user = new UserRegistration();
        user.setAlias("testuser");
        user.setIdentifier("password123");
        user.setEmail("test@example.com");
        user.setAvatarUrl("https://example.com/avatar.jpg");
        user.setAdmin(true);
        userRepository.save(user);

        // When
        UserRegistrationDTO result = userAccessService.validateUser("testuser", "password123");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getAlias());
        assertEquals("test@example.com", result.getEmail());
        assertEquals("https://example.com/avatar.jpg", result.getAvatarUrl());
        assertTrue(result.isAdmin());
    }

    @Test
    void validateUser_WithWrongPassword_ShouldReturnNull() {
        // Given
        UserRegistration user = new UserRegistration();
        user.setAlias("testuser");
        user.setIdentifier("correctPassword");
        user.setEmail("test@example.com");
        userRepository.save(user);

        // When
        UserRegistrationDTO result = userAccessService.validateUser("testuser", "wrongPassword");

        // Then
        assertNull(result);
    }

    @Test
    void validateUser_WithNonExistentUser_ShouldReturnNull() {
        // When
        UserRegistrationDTO result = userAccessService.validateUser("nonexistent", "password");

        // Then
        assertNull(result);
    }

    @Test
    void validateUser_WithUserInDatabase_ShouldReturnCorrectly() {
        // Given
        UserRegistration user = new UserRegistration();
        user.setAlias("integrationTestUser");
        user.setIdentifier("integrationPassword");
        user.setEmail("integration@test.com");
        user.setAvatarUrl("https://example.com/integration.jpg");
        user.setAdmin(false);
        UserRegistration savedUser = userRepository.save(user);

        // When
        UserRegistrationDTO result = userAccessService.validateUser("integrationTestUser", "integrationPassword");

        // Then
        assertNotNull(result);
        assertEquals("integrationTestUser", result.getAlias());
        assertEquals("integration@test.com", result.getEmail());
        assertEquals("https://example.com/integration.jpg", result.getAvatarUrl());
        assertFalse(result.isAdmin());
        assertNotNull(result.getId()); // ID should be set
    }
}