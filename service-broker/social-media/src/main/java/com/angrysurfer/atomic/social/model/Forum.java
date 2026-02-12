package com.angrysurfer.atomic.social.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.angrysurfer.atomic.social.ForumDTO;
import com.angrysurfer.atomic.social.model.User;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Document(collection = "forums")
public class Forum implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2527484659765374240L;

    @Id
    private String id;

    private String name;

    public ForumDTO toDTO() {
        ForumDTO dto = new ForumDTO();
        dto.setId(getId()); // MongoDB ObjectId as String
        dto.setName(getName());
        getMembers().forEach(member -> dto.getMembers().add(member.toDTO()));
        return dto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DBRef
    private Set<User> members = new HashSet<>();

    public Forum() {
    }

    public Forum(String name) {
        this.setName(name);
    }

    public void addMember(User user) {
        this.getMembers().add(user);
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }
}