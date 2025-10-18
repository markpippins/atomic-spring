package com.angrysurfer.atomic.user.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.angrysurfer.atomic.user.ForumDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Forum implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2527484659765374240L;

    @Id
    @SequenceGenerator(name = "forum_sequence", sequenceName = "forum_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "forum_sequence")
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Long id;

    private String name;

    public ForumDTO toDTO() {
        ForumDTO dto = new ForumDTO();
        dto.setId(getId());
        dto.setName(getName());
        getMembers().forEach(member -> dto.getMembers().add(member.toDTO()));
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany
    @JoinTable(name = "forum_members")
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
