package com.madhuka.tutor_finder_backend.controller;

import com.madhuka.tutor_finder_backend.entity.Subject;
import com.madhuka.tutor_finder_backend.entity.TutorProfile;
import com.madhuka.tutor_finder_backend.entity.User;
import com.madhuka.tutor_finder_backend.repository.SubjectRepository;
import com.madhuka.tutor_finder_backend.repository.TutorProfileRepository;
import com.madhuka.tutor_finder_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/tutors")
public class TutorController {

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    /**
     * Endpoint 1: Reveal tutor's contact details (Only authenticated users)
     * POST /api/tutors/{id}/contact
     */
    @PostMapping("/{id}/contact")
    public ResponseEntity<?> getTutorContact(@PathVariable Long id) {
        return tutorProfileRepository.findById(id)
                .map(profile -> {
                    Map<String, String> response = new HashMap<>();
                    response.put("phoneNumber", profile.getPhoneNumber() != null ? profile.getPhoneNumber() : "Not provided");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint 2: Get current logged-in tutor's profile details
     * GET /api/tutors/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<TutorProfile> getCurrentTutorProfile() {
        User user = getCurrentAuthenticatedUser();
        TutorProfile profile = tutorProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    // Automatically create a profile if it doesn't exist yet
                    TutorProfile newProfile = new TutorProfile();
                    newProfile.setUser(user);
                    newProfile.setApprovalStatus("PENDING");
                    return tutorProfileRepository.save(newProfile);
                });
        return ResponseEntity.ok(profile);
    }

    /**
     * Request DTO for updating profile details
     */
    public static class ProfileUpdateRequest {
        private String fullName;
        private String phoneNumber;
        private String location;
        private String bio;
        private List<Long> subjectIds;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public String getBio() { return bio; }
        public void setBio(String bio) { this.bio = bio; }

        public List<Long> getSubjectIds() { return subjectIds; }
        public void setSubjectIds(List<Long> subjectIds) { this.subjectIds = subjectIds; }
    }

    /**
     * Endpoint 3: Update current logged-in tutor's profile details
     * PUT /api/tutors/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateTutorProfile(@RequestBody ProfileUpdateRequest request) {
        User user = getCurrentAuthenticatedUser();
        TutorProfile profile = tutorProfileRepository.findByUser(user)
                .orElseGet(() -> {
                    TutorProfile newProfile = new TutorProfile();
                    newProfile.setUser(user);
                    newProfile.setApprovalStatus("PENDING");
                    return newProfile;
                });

        profile.setFullName(request.getFullName());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setLocation(request.getLocation());
        profile.setBio(request.getBio());

        // Resolve subject entities
        if (request.getSubjectIds() != null) {
            List<Subject> subjects = subjectRepository.findAllById(request.getSubjectIds());
            profile.setSubjects(new HashSet<>(subjects));
        } else {
            profile.setSubjects(new HashSet<>());
        }

        // Tutors updating their profile should trigger a reset to PENDING so Admin can re-verify details
        profile.setApprovalStatus("PENDING");

        TutorProfile updatedProfile = tutorProfileRepository.save(profile);
        return ResponseEntity.ok(updatedProfile);
    }

    private User getCurrentAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found with email: " + email));
    }
}
