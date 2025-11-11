package com.angrysurfer.atomic.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.angrysurfer.atomic.user.UserDTO;
import com.angrysurfer.atomic.user.UserRegistrationDTO;
import com.angrysurfer.atomic.user.model.UserRegistration;
import com.angrysurfer.atomic.user.repository.UserRegistrationRepository;

class UserRegistrationServiceTest {

    private UserRegistrationRepository userRepository;

    private UserAccessService userAccessService;
    private UserRegistration validUser;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRegistrationRepository.class);
        userAccessService = new UserAccessService(userRepository);
        
        validUser = new UserRegistration();
        validUser.setMongoId("507f1f77bcf86cd799439011");
        validUser.setId(123L);
        validUser.setAlias("testUser");
        validUser.setEmail("test@example.com");
        validUser.setIdentifier("password123");
    }

    @Test
    void testLoginSuccess() {
        when(userRepository.findByAlias("testUser")).thenReturn(Optional.of(validUser));

        UserRegistrationDTO result = userAccessService.login("testUser", "password123");

        assertNotNull(result);
        assertEquals("123", result.getId());
        assertEquals("testUser", result.getAlias());
    }

    @Test
    void testLoginFailureWrongPassword() {
        when(userRepository.findByAlias("testUser")).thenReturn(Optional.of(validUser));

        UserRegistrationDTO result = userAccessService.login("testUser", "wrongPassword");

        assertNull(result);
    }

    @Test
    void testLoginFailureUserNotFound() {
        when(userRepository.findByAlias("nonexistent")).thenReturn(Optional.empty());

        UserRegistrationDTO result = userAccessService.login("nonexistent", "password123");

        assertNull(result);
    }
}