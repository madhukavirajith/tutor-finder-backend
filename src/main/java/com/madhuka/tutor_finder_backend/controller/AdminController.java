package com.madhuka.tutor_finder_backend.controller;

import com.madhuka.tutor_finder_backend.entity.TutorProfile;
import com.madhuka.tutor_finder_backend.repository.TutorProfileRepository;
import com.madhuka.tutor_finder_backend.repository.UserRepository;
import com.madhuka.tutor_finder_backend.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

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

    /**
     * Endpoint 4: Get general statistics for Admin Dashboard
     * GET /api/admin/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getAdminStats() {
        long totalUsers = userRepository.count();
        long totalTutors = userRepository.countByRole("TUTOR");
        long totalParents = userRepository.countByRole("PARENT");
        long totalSubjects = subjectRepository.count();
        long pendingTutors = tutorProfileRepository.countByApprovalStatus("PENDING");
        long approvedTutors = tutorProfileRepository.countByApprovalStatus("APPROVED");

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalTutors", totalTutors);
        stats.put("totalParents", totalParents);
        stats.put("totalSubjects", totalSubjects);
        stats.put("pendingTutors", pendingTutors);
        stats.put("approvedTutors", approvedTutors);

        return ResponseEntity.ok(stats);
    }
}
