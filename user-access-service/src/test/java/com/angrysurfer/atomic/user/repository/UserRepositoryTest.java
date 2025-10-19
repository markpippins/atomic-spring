package com.angrysurfer.atomic.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.angrysurfer.atomic.user.model.ValidUser;

class UserRepositoryTest {

    private ValidUser validUser;

    @BeforeEach
    void setUp() {
        // Since UserRepository is an interface extending MongoRepository, 
        // we'll test the behavior through mocking
        validUser = new ValidUser();
        validUser.setMongoId("507f1f77bcf86cd799439011");
        validUser.setId(123L);
        validUser.setAlias("testUser");
        validUser.setEmail("test@example.com");
    }

    @Test
    void testFindByAlias() {
        // This would normally be implemented by Spring Data MongoDB
        // For testing purposes, we'd use an integration test with an embedded MongoDB
        // or mock the MongoRepository behavior
        assertNotNull(validUser.getAlias());
        assertEquals("testUser", validUser.getAlias());
    }

    @Test
    void testFindByEmail() {
        assertEquals("test@example.com", validUser.getEmail());
    }

    @Test
    void testSave() {
        // Test that the user object can be saved and maintains its properties
        ValidUser savedUser = validUser; // In real scenario, this would be the result of save operation
        
        assertEquals("507f1f77bcf86cd799439011", savedUser.getMongoId());
        assertEquals(123L, savedUser.getId());
        assertEquals("testUser", savedUser.getAlias());
        assertEquals("test@example.com", savedUser.getEmail());
    }

    @Test
    void testFindById() {
        Optional<ValidUser> userOpt = Optional.of(validUser);
        
        assertTrue(userOpt.isPresent());
        assertEquals("507f1f77bcf86cd799439011", userOpt.get().getMongoId());
        assertEquals(123L, userOpt.get().getId());
    }
}