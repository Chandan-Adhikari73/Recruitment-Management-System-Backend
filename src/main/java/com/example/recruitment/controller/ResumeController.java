package com.example.recruitment.controller;

import com.example.recruitment.config.JwtUtil;
import com.example.recruitment.entity.Profile;
import com.example.recruitment.entity.User;
import com.example.recruitment.repository.UserRepository;
import com.example.recruitment.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
public class ResumeController {
    @Autowired 
    private JwtUtil jwtUtil;
    
    @Autowired 
    private UserRepository userRepo;
    
    @Autowired 
    private ResumeService resumeService;

    @PostMapping("/uploadResume")
    public ResponseEntity<?> uploadResume(@RequestHeader("Authorization") String auth,
                                          @RequestPart("file") MultipartFile file) {
        try {
            String token = extractToken(auth);
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            String email = jwtUtil.getEmailFromToken(token);
            Optional<User> opt = userRepo.findByEmail(email);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            User user = opt.get();
            if (user.getUserType() != User.UserType.APPLICANT) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only applicants allowed");
            }
            
            Profile profile = resumeService.storeAndParseResume(user, file);
            return ResponseEntity.ok(profile);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private String extractToken(String authHeader) {
        if (authHeader == null) return "";
        if (authHeader.startsWith("Bearer ")) return authHeader.substring(7);
        return authHeader;
    }
}