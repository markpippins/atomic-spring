package com.angrysurfer.atomic.user.model;

import com.angrysurfer.atomic.user.UserDTO;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {

    @Test
    void user_WithValidData_ShouldSetAndGetPropertiesCorrectly() {
        // Given
        User user = new User();

        // When
        user.setId("507f1f77bcf86cd799439011");
        user.setIdentifier("password123");
        user.setAlias("testuser");
        user.setEmail("test@example.com");
        user.setAvatarUrl("https://example.com/avatar.jpg");
        user.setAdmin(true);

        // Then
        assertEquals("507f1f77bcf86cd799439011", user.getId());
        assertEquals("password123", user.getIdentifier());
        assertEquals("testuser", user.getAlias());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("https://example.com/avatar.jpg", user.getAvatarUrl());
        assertTrue(user.isAdmin());
    }

    @Test
    void user_WithConstructor_ShouldInitializeCorrectly() {
        // When
        User user = new User("testuser", "test@example.com", "https://example.com/avatar.jpg");

        // Then
        assertEquals("testuser", user.getAlias());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("https://example.com/avatar.jpg", user.getAvatarUrl());
        assertNull(user.getIdentifier()); // Not set by this constructor
    }

    @Test
    void user_WithConstructorWithIdentifier_ShouldInitializeCorrectly() {
        // When
        User user = new User("testuser", "test@example.com", "https://example.com/avatar.jpg", "password123");

        // Then
        assertEquals("testuser", user.getAlias());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("https://example.com/avatar.jpg", user.getAvatarUrl());
        assertEquals("password123", user.getIdentifier());
    }

    @Test
    void user_ToDto_ShouldConvertCorrectly() {
        // Given
        User user = new User();
        user.setId("507f1f77bcf86cd799439011");
        user.setAlias("dtoUser");
        user.setEmail("dto@example.com");
        user.setIdentifier("dtoPassword");
        user.setAvatarUrl("https://example.com/dto.jpg");
        user.setAdmin(true);

        // Add some followers, following, and friends
        User follower = new User();
        follower.setAlias("follower1");
        User following = new User();
        following.setAlias("following1");
        User friend = new User();
        friend.setAlias("friend1");

        Set<User> followers = new HashSet<>();
        followers.add(follower);
        Set<User> followingSet = new HashSet<>();
        followingSet.add(following);
        Set<User> friends = new HashSet<>();
        friends.add(friend);

        user.setFollowers(followers);
        user.setFollowing(followingSet);
        user.setFriends(friends);

        // When
        UserDTO dto = user.toDTO();

        // Then
        assertNotNull(dto);
        assertEquals("507f1f77bcf86cd799439011", dto.getId());
        assertEquals("dtoUser", dto.getAlias());
        assertEquals("dto@example.com", dto.getEmail());
        assertEquals("dtoPassword", dto.getIdentifier());
        assertEquals("https://example.com/dto.jpg", dto.getAvatarUrl());
        assertTrue(dto.isAdmin());
        assertEquals(1, dto.getFollowers().size());
        assertTrue(dto.getFollowers().contains("follower1"));
        assertEquals(1, dto.getFollowing().size());
        assertTrue(dto.getFollowing().contains("following1"));
        assertEquals(1, dto.getFriends().size());
        assertTrue(dto.getFriends().contains("friend1"));
    }

    @Test
    void user_ToDto_WithEmptyRelationships_ShouldHandleGracefully() {
        // Given
        User user = new User();
        user.setAlias("dtoUser");
        user.setEmail("dto@example.com");

        // When
        UserDTO dto = user.toDTO();

        // Then
        assertNotNull(dto);
        assertEquals("dtoUser", dto.getAlias());
        assertEquals("dto@example.com", dto.getEmail());
        assertEquals(0, dto.getFollowers().size());
        assertEquals(0, dto.getFollowing().size());
        assertEquals(0, dto.getFriends().size());
    }

    @Test
    void user_DefaultAvatarUrl_ShouldBeSet() {
        // Given
        User user = new User();

        // Then
        assertEquals("https://picsum.photos/50/50", user.getAvatarUrl());
    }
}