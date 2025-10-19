package com.angrysurfer.atomic.user.model;

import java.io.Serializable;

import com.angrysurfer.atomic.user.UserDTO;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Document(collection = "users")
public class ValidUser implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2747813660378401172L;

    @Id
    private String mongoId;
    
    // Client-facing ID - for compatibility with existing web clients
    private Long id;

    private String identifier;

    @NotBlank(message = "Alias is required")
    private String alias;

    @Email(message = "Email should be valid")
    private String email;

    private String avatarUrl = "https://picsum.photos/50/50";

    public UserDTO toDTO() {
        UserDTO dto = new UserDTO();
        dto.setId(String.valueOf(getId())); // Convert Long to String for DTO
        dto.setAlias(getAlias());
        dto.setEmail(getEmail());
        dto.setIdentifier(getIdentifier());
        dto.setAvatarUrl(getAvatarUrl());
        return dto;
    }

    public ValidUser() {

    }

    public ValidUser(String alias, String email, String avatarUrl) {
        setAlias(alias);
        setEmail(email);
        setAvatarUrl(avatarUrl);
    }

    public ValidUser(String alias, String email, String avatarUrl, String identifier) {
        setAlias(alias);
        setEmail(email);
        setAvatarUrl(avatarUrl);
        setIdentifier(identifier);
    }

    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}