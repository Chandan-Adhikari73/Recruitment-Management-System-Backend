package com.example.recruitment.controller;

import com.example.recruitment.config.JwtUtil;
import com.example.recruitment.entity.*;
import com.example.recruitment.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/jobs")
public class JobController {
    @Autowired private JobRepository jobRepo;
    @Autowired private JobApplicationRepository appRepo;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepo;

    private String extractToken(String auth) {
        if (auth == null) return "";
        if (auth.startsWith("Bearer ")) return auth.substring(7);
        return auth;
    }

    @GetMapping
    public ResponseEntity<?> listJobs(@RequestHeader(value = "Authorization", required = false) String auth) {
        String token = extractToken(auth);
        if (!jwtUtil.validateToken(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(jobRepo.findAll());
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyToJob(@RequestHeader("Authorization") String auth, @RequestParam Long job_id) {
        String token = extractToken(auth);
        if (!jwtUtil.validateToken(token)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        
        String role = jwtUtil.getRoleFromToken(token);
        if (!"APPLICANT".equalsIgnoreCase(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only applicants can apply");
        }
        
        String email = jwtUtil.getEmailFromToken(token);
        Optional<User> opt = userRepo.findByEmail(email);
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        
        User u = opt.get();
        Optional<Job> jobOpt = jobRepo.findById(job_id);
        if (jobOpt.isEmpty()) return ResponseEntity.notFound().build();
        
        Job job = jobOpt.get();
        JobApplication ja = new JobApplication();
        ja.setApplicant(u);
        ja.setJob(job);
        appRepo.save(ja);
        
        job.setTotalApplications(job.getTotalApplications() + 1);
        jobRepo.save(job);
        return ResponseEntity.ok("Applied");
    }
}