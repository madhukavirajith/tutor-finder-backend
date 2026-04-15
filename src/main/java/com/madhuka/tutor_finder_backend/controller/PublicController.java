package com.madhuka.tutor_finder_backend.controller;

import com.madhuka.tutor_finder_backend.entity.Subject;
import com.madhuka.tutor_finder_backend.entity.TutorProfile;
import com.madhuka.tutor_finder_backend.repository.SubjectRepository;
import com.madhuka.tutor_finder_backend.repository.TutorProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
//@CrossOrigin(origins = "http://localhost:3000")//
public class PublicController {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        return ResponseEntity.ok(subjectRepository.findAll());
    }

    @GetMapping("/tutors")
    public ResponseEntity<Page<TutorProfile>> getApprovedTutors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String subject) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName").ascending());
        Page<TutorProfile> tutors;

        if (search != null && !search.trim().isEmpty()) {
            tutors = tutorProfileRepository.searchApprovedTutors(search.trim(), pageable);
        } else {
            tutors = tutorProfileRepository.findByApprovalStatus("APPROVED", pageable);
        }

        // Optional: filter by location or subject if needed (can be added later)
        return ResponseEntity.ok(tutors);
    }

    @GetMapping("/tutors/{id}")
    public ResponseEntity<TutorProfile> getTutorById(@PathVariable Long id) {
        return tutorProfileRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}