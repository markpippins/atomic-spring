package com.angrysurfer.atomic.user.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.angrysurfer.atomic.user.ProfileDTO;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Document(collection = "profiles")
public class Profile implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6188258652004048094L;

    @Id
    private String id;

    private String firstName;

    private String lastName;

    private String city;

    private String state;

    private String profileImageUrl;

    @DBRef
    private User user;

    @DBRef
    private Set<Interest> interests = new HashSet<>();

    public ProfileDTO toDTO() {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(getId()); // MongoDB ObjectId as String
        dto.setFirstName(getFirstName());
        dto.setLastName(getLastName());
        dto.setCity(getCity());
        dto.setState(getState());
        dto.setProfileImageUrl(getProfileImageUrl());
        getInterests().forEach(interest -> dto.getInterests().add(interest.getName()));
        return dto;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Set<Interest> getInterests() {
        return interests;
    }

    public void setInterests(Set<Interest> interests) {
        this.interests = interests;
    }

    public User getUser() {
        return user;
    }

}