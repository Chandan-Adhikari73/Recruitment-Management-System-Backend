package com.example.recruitment.service;

import com.example.recruitment.entity.Profile;
import com.example.recruitment.entity.User;
import com.example.recruitment.repository.ProfileRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.*;
import java.util.*;

@Service
public class ResumeService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${apilayer.api.key}")
    private String apiKey;

    @Value("${apilayer.endpoint}")
    private String apiEndpoint;

    private final ProfileRepository profileRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public ResumeService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile storeAndParseResume(User applicant, MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null) throw new IllegalArgumentException("Invalid file");
        
        String lowerFileName = fileName.toLowerCase();
        if (!(lowerFileName.endsWith(".pdf") || lowerFileName.endsWith(".docx"))) {
            throw new IllegalArgumentException("Only PDF or DOCX files are allowed");
        }

        // Create upload directory if it doesn't exist
        Files.createDirectories(Paths.get(uploadDir));
        
        // Generate unique file name
        String storedPath = uploadDir + "/" + UUID.randomUUID() + "_" + fileName;
        Files.copy(file.getInputStream(), Paths.get(storedPath), StandardCopyOption.REPLACE_EXISTING);

        // Call resume parsing API
        String parsedData = callResumeParserAPI(file);
        Profile profile = parseResumeData(applicant, storedPath, parsedData);
        
        return profileRepository.save(profile);
    }

    private String callResumeParserAPI(MultipartFile file) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", apiKey);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        byte[] fileBytes = file.getBytes();
        HttpEntity<byte[]> entity = new HttpEntity<>(fileBytes, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                apiEndpoint, 
                HttpMethod.POST, 
                entity, 
                String.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("API returned status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to call resume parser API: " + e.getMessage());
        }
    }

    private Profile parseResumeData(User applicant, String storedPath, String apiResponse) throws Exception {
        // Find existing profile or create new one
        Optional<Profile> existingProfileOpt = profileRepository.findByApplicantId(applicant.getId());
        Profile profile = existingProfileOpt.orElse(new Profile());
        
        profile.setApplicant(applicant);
        profile.setResumeFileAddress(storedPath);

        if (apiResponse == null || apiResponse.isEmpty()) {
            return profile; // Return profile without parsed data if API failed
        }

        JsonNode rootNode = mapper.readTree(apiResponse);

        // Parse SKILLS (array of strings)
        if (rootNode.has("skills") && rootNode.get("skills").isArray()) {
            List<String> skillsList = new ArrayList<>();
            rootNode.get("skills").forEach(skillNode -> skillsList.add(skillNode.asText()));
            profile.setSkills(String.join(", ", skillsList));
        } else if (rootNode.has("skills")) {
            profile.setSkills(rootNode.get("skills").asText());
        }

        // Parse EDUCATION (array of objects with "name")
        if (rootNode.has("education") && rootNode.get("education").isArray()) {
            List<String> educationList = new ArrayList<>();
            rootNode.get("education").forEach(eduNode -> {
                if (eduNode.has("name")) {
                    educationList.add(eduNode.get("name").asText());
                }
            });
            profile.setEducation(String.join(", ", educationList));
        } else if (rootNode.has("education")) {
            profile.setEducation(rootNode.get("education").asText());
        }

        // Parse EXPERIENCE (array of objects with "name" and "dates")
        if (rootNode.has("experience") && rootNode.get("experience").isArray()) {
            List<String> experienceList = new ArrayList<>();
            rootNode.get("experience").forEach(expNode -> {
                StringBuilder expBuilder = new StringBuilder();
                
                if (expNode.has("name")) {
                    expBuilder.append(expNode.get("name").asText());
                }
                
                if (expNode.has("dates") && expNode.get("dates").isArray()) {
                    List<String> datesList = new ArrayList<>();
                    expNode.get("dates").forEach(dateNode -> datesList.add(dateNode.asText()));
                    if (!datesList.isEmpty()) {
                        expBuilder.append(" (").append(String.join(", ", datesList)).append(")");
                    }
                }
                
                if (expBuilder.length() > 0) {
                    experienceList.add(expBuilder.toString());
                }
            });
            profile.setExperience(String.join("; ", experienceList));
        } else if (rootNode.has("experience")) {
            profile.setExperience(rootNode.get("experience").asText());
        }

        // Parse personal information
        if (rootNode.has("name")) {
            profile.setName(rootNode.get("name").asText());
        } else {
            profile.setName(applicant.getName()); // Fallback to user's name
        }
        
        if (rootNode.has("email")) {
            profile.setEmail(rootNode.get("email").asText());
        } else {
            profile.setEmail(applicant.getEmail()); // Fallback to user's email
        }
        
        if (rootNode.has("phone")) {
            profile.setPhone(rootNode.get("phone").asText());
        }

        return profile;
    }
}