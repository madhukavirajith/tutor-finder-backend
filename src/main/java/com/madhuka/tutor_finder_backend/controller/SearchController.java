package com.madhuka.tutor_finder_backend.controller;

import com.madhuka.tutor_finder_backend.dsa.LevenshteinMatchStrategy;
import com.madhuka.tutor_finder_backend.dsa.RecommendationEngine;
import com.madhuka.tutor_finder_backend.dsa.SubjectSearchTrie;
import com.madhuka.tutor_finder_backend.entity.Subject;
import com.madhuka.tutor_finder_backend.entity.TutorProfile;
import com.madhuka.tutor_finder_backend.repository.SubjectRepository;
import com.madhuka.tutor_finder_backend.repository.TutorProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ============================================================================
 * INTERVIEW TOPIC: SEARCH CONTROLLER - CONNECTING CORE APIS TO CUSTOM DSA
 * ============================================================================
 * Exposes REST endpoints to query Trie prefix indices, calculate string distances
 * dynamically via Dynamic Programming, and rank matching tutors using PriorityQueue heaps.
 * ============================================================================
 */
@RestController
@RequestMapping("/api/public/search")
public class SearchController {

    @Autowired
    private SubjectSearchTrie subjectSearchTrie;

    @Autowired
    private LevenshteinMatchStrategy levenshteinMatchStrategy;

    @Autowired
    private RecommendationEngine recommendationEngine;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    /**
     * Endpoint 1: Real-time dropdown subject autocomplete (Trie)
     * GET /api/public/search/autocomplete?prefix=math
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> getAutocompleteSuggestions(@RequestParam String prefix) {
        List<String> suggestions = subjectSearchTrie.autocomplete(prefix);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Endpoint 2: Did-You-Mean Auto-Correction (Dynamic Programming - Levenshtein Distance)
     * GET /api/public/search/correct?query=maths
     */
    @GetMapping("/correct")
    public ResponseEntity<Map<String, Object>> getTypoCorrection(@RequestParam String query) {
        Map<String, Object> response = new HashMap<>();
        if (query == null || query.trim().isEmpty()) {
            response.put("corrected", false);
            return ResponseEntity.ok(response);
        }

        List<Subject> subjects = subjectRepository.findAll();
        String bestMatch = null;
        double highestSimilarity = 0.0;
        double threshold = 0.5; // Similarity threshold for suggestion (at least 50% match)

        for (Subject subject : subjects) {
            double similarity = levenshteinMatchStrategy.calculateSimilarity(query, subject.getName());
            if (similarity > highestSimilarity) {
                highestSimilarity = similarity;
                bestMatch = subject.getName();
            }
        }

        // Suggest only if it's close but not an exact 100% match
        if (highestSimilarity >= threshold && highestSimilarity < 0.99) {
            response.put("corrected", true);
            response.put("suggestion", bestMatch);
            response.put("similarity", highestSimilarity);
        } else {
            response.put("corrected", false);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint 3: Smart Tutor Recommendation Ranking (PriorityQueue Max-Heap)
     * GET /api/public/search/recommend
     */
    @GetMapping("/recommend")
    public ResponseEntity<List<TutorProfile>> getTutorRecommendations(
            @RequestParam(required = false, defaultValue = "") String location,
            @RequestParam(required = false, defaultValue = "") String subject,
            @RequestParam(required = false, defaultValue = "") String bioKeyword,
            @RequestParam(required = false, defaultValue = "HYBRID") String mode,
            @RequestParam(required = false, defaultValue = "9") int limit) {

        // Fetch all approved tutor profiles from database
        List<TutorProfile> allApprovedTutors = tutorProfileRepository.findByApprovalStatus("APPROVED");

        // OOP Concept: Builder pattern to construct Recommendation Request
        RecommendationEngine.Request request = new RecommendationEngine.Request.Builder()
                .preferredLocation(location)
                .subjectKeyword(subject)
                .searchBioKeyword(bioKeyword)
                .mode(mode)
                .limit(limit)
                .build();

        // Run Recommendation Engine using Max-Heap
        List<RecommendationEngine.ScoredTutor> recommended =
                recommendationEngine.recommendTutors(allApprovedTutors, request);

        // Map scored items back to tutor profile list for API response
        List<TutorProfile> profiles = recommended.stream()
                .map(RecommendationEngine.ScoredTutor::getProfile)
                .collect(Collectors.toList());

        return ResponseEntity.ok(profiles);
    }
}
