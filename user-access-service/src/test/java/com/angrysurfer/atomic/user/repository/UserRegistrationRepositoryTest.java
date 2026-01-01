package com.angrysurfer.atomic.user.repository;

import com.angrysurfer.atomic.user.model.UserRegistration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRegistrationRepositoryTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private UserRegistrationRepository userRegistrationRepository;

    @Test
    void findByAlias_WithExistingUser_ShouldReturnUser() {
        // Given
        UserRegistration expectedUser = new UserRegistration();
        expectedUser.setAlias("testuser");
        when(mongoTemplate.findOne(any(Query.class), eq(UserRegistration.class))).thenReturn(expectedUser);

        // When
        Optional<UserRegistration> result = userRegistrationRepository.findByAlias("testuser");

        // Then
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getAlias());
        verify(mongoTemplate).findOne(any(Query.class), eq(UserRegistration.class));
    }

    @Test
    void findByAlias_WithNonExistingUser_ShouldReturnEmpty() {
        // Given
        when(mongoTemplate.findOne(any(Query.class), eq(UserRegistration.class))).thenReturn(null);

        // When
        Optional<UserRegistration> result = userRegistrationRepository.findByAlias("nonexistent");

        // Then
        assertFalse(result.isPresent());
        verify(mongoTemplate).findOne(any(Query.class), eq(UserRegistration.class));
    }

    @Test
    void findByEmail_WithExistingUser_ShouldReturnUser() {
        // Given
        UserRegistration expectedUser = new UserRegistration();
        expectedUser.setEmail("test@example.com");
        when(mongoTemplate.findOne(any(Query.class), eq(UserRegistration.class))).thenReturn(expectedUser);

        // When
        Optional<UserRegistration> result = userRegistrationRepository.findByEmail("test@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        verify(mongoTemplate).findOne(any(Query.class), eq(UserRegistration.class));
    }

    @Test
    void findByEmail_WithNonExistingUser_ShouldReturnEmpty() {
        // Given
        when(mongoTemplate.findOne(any(Query.class), eq(UserRegistration.class))).thenReturn(null);

        // When
        Optional<UserRegistration> result = userRegistrationRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(result.isPresent());
        verify(mongoTemplate).findOne(any(Query.class), eq(UserRegistration.class));
    }
}