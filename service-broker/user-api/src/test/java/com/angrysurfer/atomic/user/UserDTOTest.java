package com.angrysurfer.atomic.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class UserDTOTest {

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
    }

    @Test
    void testId() {
        String expectedId = "123";
        userDTO.setId(expectedId);
        
        assertEquals(expectedId, userDTO.getId());
    }

    @Test
    void testAlias() {
        String expectedAlias = "testUser";
        userDTO.setAlias(expectedAlias);
        
        assertEquals(expectedAlias, userDTO.getAlias());
    }

    @Test
    void testIdentifier() {
        String expectedIdentifier = "secret123";
        userDTO.setIdentifier(expectedIdentifier);
        
        assertEquals(expectedIdentifier, userDTO.getIdentifier());
    }

    @Test
    void testEmail() {
        String expectedEmail = "test@example.com";
        userDTO.setEmail(expectedEmail);
        
        assertEquals(expectedEmail, userDTO.getEmail());
    }

    @Test
    void testAvatarUrl() {
        String expectedAvatarUrl = "https://example.com/avatar.jpg";
        userDTO.setAvatarUrl(expectedAvatarUrl);
        
        assertEquals(expectedAvatarUrl, userDTO.getAvatarUrl());
    }

    @Test
    void testFollowers() {
        Set<String> followers = new HashSet<>();
        followers.add("follower1");
        userDTO.setFollowers(followers);
        
        assertEquals(followers, userDTO.getFollowers());
        assertTrue(userDTO.getFollowers().contains("follower1"));
    }

    @Test
    void testFollowing() {
        Set<String> following = new HashSet<>();
        following.add("following1");
        userDTO.setFollowing(following);
        
        assertEquals(following, userDTO.getFollowing());
        assertTrue(userDTO.getFollowing().contains("following1"));
    }

    @Test
    void testFriends() {
        Set<String> friends = new HashSet<>();
        friends.add("friend1");
        userDTO.setFriends(friends);
        
        assertEquals(friends, userDTO.getFriends());
        assertTrue(userDTO.getFriends().contains("friend1"));
    }

    @Test
    void testGroups() {
        Set<String> groups = new HashSet<>();
        groups.add("group1");
        userDTO.setGroups(groups);
        
        assertEquals(groups, userDTO.getGroups());
        assertTrue(userDTO.getGroups().contains("group1"));
    }

    @Test
    void testInterests() {
        Set<String> interests = new HashSet<>();
        interests.add("Technology");
        userDTO.setInterests(interests);
        
        assertEquals(interests, userDTO.getInterests());
        assertTrue(userDTO.getInterests().contains("Technology"));
    }

    @Test
    void testOrganizations() {
        Set<String> organizations = new HashSet<>();
        organizations.add("Org1");
        userDTO.setOrganizations(organizations);
        
        assertEquals(organizations, userDTO.getOrganizations());
        assertTrue(userDTO.getOrganizations().contains("Org1"));
    }

    @Test
    void testProjects() {
        Set<String> projects = new HashSet<>();
        projects.add("Project1");
        userDTO.setProjects(projects);
        
        assertEquals(projects, userDTO.getProjects());
        assertTrue(userDTO.getProjects().contains("Project1"));
    }

    @Test
    void testRoles() {
        Set<String> roles = new HashSet<>();
        roles.add("Role1");
        userDTO.setRoles(roles);
        
        assertEquals(roles, userDTO.getRoles());
        assertTrue(userDTO.getRoles().contains("Role1"));
    }

    @Test
    void testTeams() {
        Set<String> teams = new HashSet<>();
        teams.add("Team1");
        userDTO.setTeams(teams);
        
        assertEquals(teams, userDTO.getTeams());
        assertTrue(userDTO.getTeams().contains("Team1"));
    }

    @Test
    void testTags() {
        Set<String> tags = new HashSet<>();
        tags.add("Tag1");
        userDTO.setTags(tags);
        
        assertEquals(tags, userDTO.getTags());
        assertTrue(userDTO.getTags().contains("Tag1"));
    }

    @Test
    void testDefaultValues() {
        UserDTO dto = new UserDTO();
        
        // Test default empty collections
        assertNotNull(dto.getFollowers());
        assertNotNull(dto.getFollowing());
        assertNotNull(dto.getFriends());
        assertNotNull(dto.getGroups());
        assertNotNull(dto.getInterests());
        assertNotNull(dto.getOrganizations());
        assertNotNull(dto.getProjects());
        assertNotNull(dto.getRoles());
        assertNotNull(dto.getTeams());
        assertNotNull(dto.getTags());
        
        // Test default empty collections are empty
        assertTrue(dto.getFollowers().isEmpty());
        assertTrue(dto.getFollowing().isEmpty());
        assertTrue(dto.getFriends().isEmpty());
        assertTrue(dto.getGroups().isEmpty());
        assertTrue(dto.getInterests().isEmpty());
        assertTrue(dto.getOrganizations().isEmpty());
        assertTrue(dto.getProjects().isEmpty());
        assertTrue(dto.getRoles().isEmpty());
        assertTrue(dto.getTeams().isEmpty());
        assertTrue(dto.getTags().isEmpty());
    }
}