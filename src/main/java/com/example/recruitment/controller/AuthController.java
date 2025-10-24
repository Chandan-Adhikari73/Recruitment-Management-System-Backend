package com.example.recruitment.controller;

import com.example.recruitment.config.JwtUtil;
import com.example.recruitment.dto.AuthRequest;
import com.example.recruitment.dto.SignupRequest;
import com.example.recruitment.entity.User;
import com.example.recruitment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/")
public class AuthController {
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        if (userRepo.findByEmail(req.email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email exists");
        }
        
        User u = new User();
        u.setName(req.name);
        u.setEmail(req.email);
        u.setPasswordHash(encoder.encode(req.password));
        u.setAddress(req.address);
        u.setProfileHeadline(req.profileHeadline);
        u.setUserType("ADMIN".equalsIgnoreCase(req.userType) ? User.UserType.ADMIN : User.UserType.APPLICANT);
        userRepo.save(u);
        return ResponseEntity.ok("Created");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        Optional<User> opt = userRepo.findByEmail(req.email);
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        
        User u = opt.get();
        if (!encoder.matches(req.password, u.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
        
        String token = jwtUtil.generateToken(u.getEmail(), u.getUserType().name());
        Map<String, String> m = new HashMap<>();
        m.put("token", token);
        return ResponseEntity.ok(m);
    }
}