package com.example.recruitment.repository;

import com.example.recruitment.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    
    @Query("SELECT p FROM Profile p WHERE p.applicant.id = :applicantId")
    Optional<Profile> findByApplicantId(@Param("applicantId") Long applicantId);
}