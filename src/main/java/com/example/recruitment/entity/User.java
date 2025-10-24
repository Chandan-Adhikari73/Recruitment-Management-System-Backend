package com.example.recruitment.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @Column(unique = true)
    private String email;
    
    private String passwordHash;
    private String address;
    private String profileHeadline;
    
    @Enumerated(EnumType.STRING)
    private UserType userType;

    public enum UserType { 
        APPLICANT, 
        ADMIN 
    }

    // ALL GETTER METHODS
    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public String getAddress() {
        return this.address;
    }

    public String getProfileHeadline() {
        return this.profileHeadline;
    }

    public UserType getUserType() {
        return this.userType;
    }

    // ALL SETTER METHODS
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setProfileHeadline(String profileHeadline) {
        this.profileHeadline = profileHeadline;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}