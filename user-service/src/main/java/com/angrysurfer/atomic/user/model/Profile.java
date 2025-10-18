package com.angrysurfer.atomic.user.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.angrysurfer.atomic.user.ProfileDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;

@Entity(name = "Profile")
public class Profile implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6188258652004048094L;

    @Id
    @SequenceGenerator(name = "profile_sequence", sequenceName = "profile_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_sequence")
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;

    private String firstName;

    private String lastName;

    private String city;

    private String state;

    private String profileImageUrl;

    @OneToOne(fetch = FetchType.EAGER)
    private User user;

    @OneToMany(fetch = FetchType.LAZY)
    private Set<Interest> interests = new HashSet<>();

    public ProfileDTO toDTO() {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(getId());
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
