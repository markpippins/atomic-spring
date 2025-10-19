package com.angrysurfer.atomic.user.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.angrysurfer.atomic.user.UserDTO;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Document(collection = "users")
public class User implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2747813660378401172L;

    @Id
    private String id;

    private String identifier;

    private String alias;

    private String email;

    private String avatarUrl = "https://picsum.photos/50/50";

    @DBRef
    private Profile profile;

    @DBRef
    private Set<User> followers = new HashSet<>();

    @DBRef
    private Set<User> following = new HashSet<>();

    @DBRef
    private Set<User> friends = new HashSet<>();

    public UserDTO toDTO() {
        UserDTO dto = new UserDTO();
        dto.setId(getId()); // MongoDB ObjectId as String
        dto.setAlias(getAlias());
        dto.setEmail(getEmail());
        dto.setIdentifier(getIdentifier());
        dto.setAvatarUrl(getAvatarUrl());
        dto.setFollowers(getFollowers().stream().map(f -> f.getAlias()).collect(Collectors.toSet()));
        dto.setFollowing(getFollowing().stream().map(f -> f.getAlias()).collect(Collectors.toSet()));
        dto.setFriends(getFriends().stream().map(f -> f.getAlias()).collect(Collectors.toSet()));
        return dto;
    }

    public User() {

    }

    public User(String alias, String email, String avatarUrl) {
        setAlias(alias);
        setEmail(email);
        setAvatarUrl(avatarUrl);
    }

    public User(String alias, String email, String avatarUrl, String identifier) {
        setAlias(alias);
        setEmail(email);
        setAvatarUrl(avatarUrl);
        setIdentifier(identifier);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    public Set<User> getFriends() {
        return friends;
    }

    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}