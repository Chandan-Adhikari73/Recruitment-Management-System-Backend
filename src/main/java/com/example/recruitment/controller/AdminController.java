package com.example.recruitment.controller;

import com.example.recruitment.config.JwtUtil;
import com.example.recruitment.entity.Job;
import com.example.recruitment.entity.Profile;
import com.example.recruitment.entity.User;
import com.example.recruitment.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepo;
    @Autowired private JobRepository jobRepo;
    @Autowired private JobApplicationRepository appRepo;
    @Autowired private ProfileRepository profileRepo;

    private boolean isAdmin(String token) {
        return jwtUtil.validateToken(token) && "ADMIN".equalsIgnoreCase(jwtUtil.getRoleFromToken(token));
    }

    private String getEmail(String token) { 
        return jwtUtil.getEmailFromToken(token); 
    }

    @PostMapping("/job")
    public ResponseEntity<?> createJob(@RequestHeader("Authorization") String auth, @RequestBody Job job) {
        String token = extract(auth);
        if (!isAdmin(token)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin only");
        
        Optional<User> opp = userRepo.findByEmail(getEmail(token));
        opp.ifPresent(job::setPostedBy);
        job.setPostedOn(Instant.now());
        job.setTotalApplications(0);
        jobRepo.save(job);
        return ResponseEntity.ok(job);
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getJob(@RequestHeader("Authorization") String auth, @PathVariable Long jobId) {
        String token = extract(auth);
        if (!isAdmin(token)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin only");
        
        Optional<Job> j = jobRepo.findById(jobId);
        if (j.isEmpty()) return ResponseEntity.notFound().build();
        
        Map<String, Object> resp = new HashMap<>();
        resp.put("job", j.get());
        resp.put("applicants", appRepo.findByJobId(jobId));
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/applicants")
    public ResponseEntity<?> allUsers(@RequestHeader("Authorization") String auth) {
        String token = extract(auth);
        if (!isAdmin(token)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin only");
        return ResponseEntity.ok(userRepo.findAll());
    }

    @GetMapping("/applicant/{applicantId}")
    public ResponseEntity<?> getApplicant(@RequestHeader("Authorization") String auth, @PathVariable Long applicantId) {
        String token = extract(auth);
        if (!isAdmin(token)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin only");
        
        Optional<Profile> profileOpt = profileRepo.findByApplicantId(applicantId);
        if (profileOpt.isEmpty()) return ResponseEntity.notFound().build();
        
        return ResponseEntity.ok(profileOpt.get());
    }

    private String extract(String auth) {
        if (auth == null) return "";
        if (auth.startsWith("Bearer ")) return auth.substring(7);
        return auth;
    }
}