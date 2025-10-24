package com.example.recruitment.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User applicant;

    private String resumeFileAddress;
    
    @Column(columnDefinition = "TEXT")
    private String skills;
    
    @Column(columnDefinition = "TEXT")
    private String education;
    
    @Column(columnDefinition = "TEXT")
    private String experience;
    
    private String name;
    private String email;
    private String phone;

    // ALL GETTER METHODS
    public Long getId() {
        return this.id;
    }

    public User getApplicant() {
        return this.applicant;
    }

    public String getResumeFileAddress() {
        return this.resumeFileAddress;
    }

    public String getSkills() {
        return this.skills;
    }

    public String getEducation() {
        return this.education;
    }

    public String getExperience() {
        return this.experience;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    // ALL SETTER METHODS
    public void setId(Long id) {
        this.id = id;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public void setResumeFileAddress(String resumeFileAddress) {
        this.resumeFileAddress = resumeFileAddress;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}