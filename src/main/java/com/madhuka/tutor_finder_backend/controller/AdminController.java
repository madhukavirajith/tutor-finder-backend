package com.madhuka.tutor_finder_backend.controller;

import com.madhuka.tutor_finder_backend.entity.TutorProfile;
import com.madhuka.tutor_finder_backend.repository.TutorProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    /**
     * Endpoint 1: Retrieve all tutor profiles waiting for approval
     * GET /api/admin/tutors/pending
     */
    @GetMapping("/tutors/pending")
    public ResponseEntity<List<TutorProfile>> getPendingTutors() {
        List<TutorProfile> pendingTutors = tutorProfileRepository.findByApprovalStatus("PENDING");
        return ResponseEntity.ok(pendingTutors);
    }

    /**
     * Endpoint 2: Approve a tutor's profile
     * POST /api/admin/tutors/{id}/approve
     */
    @PostMapping("/tutors/{id}/approve")
    public ResponseEntity<?> approveTutor(@PathVariable Long id) {
        return tutorProfileRepository.findById(id)
                .map(profile -> {
                    profile.setApprovalStatus("APPROVED");
                    TutorProfile updated = tutorProfileRepository.save(profile);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint 3: Reject a tutor's profile
     * POST /api/admin/tutors/{id}/reject
     */
    @PostMapping("/tutors/{id}/reject")
    public ResponseEntity<?> rejectTutor(@PathVariable Long id) {
        return tutorProfileRepository.findById(id)
                .map(profile -> {
                    profile.setApprovalStatus("REJECTED");
                    TutorProfile updated = tutorProfileRepository.save(profile);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
