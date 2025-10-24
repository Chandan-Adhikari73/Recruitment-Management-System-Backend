package com.example.recruitment.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "job_applications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobApplication {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Job job;

    @ManyToOne
    private User applicant;

    private Instant appliedOn = Instant.now();

    // ADD THESE MISSING GETTER AND SETTER METHODS
    public User getApplicant() {
        return this.applicant;
    }

    public void setApplicant(User applicant) {
        this.applicant = applicant;
    }

    public Job getJob() {
        return this.job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getAppliedOn() {
        return this.appliedOn;
    }

    public void setAppliedOn(Instant appliedOn) {
        this.appliedOn = appliedOn;
    }
}