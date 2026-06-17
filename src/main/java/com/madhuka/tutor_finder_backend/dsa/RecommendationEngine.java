package com.madhuka.tutor_finder_backend.dsa;

import com.madhuka.tutor_finder_backend.entity.TutorProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * ============================================================================
 * INTERVIEW TOPIC: HEAP (PriorityQueue), FACTORY & BUILDER PATTERNS
 * ============================================================================
 * 1. FACTORY PATTERN: Allows creation of different scoring strategy comparators
 *    dynamically based on the selected recommendation mode (e.g., LOCATION, BIO, HYBRID).
 * 
 * 2. BUILDER PATTERN: The Request inner class uses a fluent builder API to construct
 *    inputs cleanly and readably.
 * 
 * 3. HEAP / PRIORITY QUEUE (DSA): We load all tutors into a Max-Heap (PriorityQueue
 *    with custom Comparator) to sort and fetch the top K tutors.
 *    - Insertion: O(log N) per element. Total creation: O(N log N).
 *    - Polling Top K: O(K log N).
 * ============================================================================
 */
@Component
public class RecommendationEngine {

    @Autowired
    private LevenshteinMatchStrategy levenshteinMatchStrategy;

    // --- OOP CONCEPT: BUILDER PATTERN ---
    public static class Request {
        private final String preferredLocation;
        private final String subjectKeyword;
        private final String searchBioKeyword;
        private final String mode; // "LOCATION", "BIO", "HYBRID"
        private final int limit;

        private Request(Builder builder) {
            this.preferredLocation = builder.preferredLocation;
            this.subjectKeyword = builder.subjectKeyword;
            this.searchBioKeyword = builder.searchBioKeyword;
            this.mode = builder.mode;
            this.limit = builder.limit;
        }

        public static class Builder {
            private String preferredLocation = "";
            private String subjectKeyword = "";
            private String searchBioKeyword = "";
            private String mode = "HYBRID";
            private int limit = 5;

            public Builder preferredLocation(String location) {
                this.preferredLocation = location;
                return this;
            }

            public Builder subjectKeyword(String subject) {
                this.subjectKeyword = subject;
                return this;
            }

            public Builder searchBioKeyword(String keyword) {
                this.searchBioKeyword = keyword;
                return this;
            }

            public Builder mode(String mode) {
                this.mode = mode;
                return this;
            }

            public Builder limit(int limit) {
                this.limit = limit;
                return this;
            }

            public Request build() {
                return new Request(this);
            }
        }
    }

    // Class to couple a TutorProfile with its calculated match score
    public static class ScoredTutor {
        private final TutorProfile profile;
        private final double score;

        public ScoredTutor(TutorProfile profile, double score) {
            this.profile = profile;
            this.score = score;
        }

        public TutorProfile getProfile() {
            return profile;
        }

        public double getScore() {
            return score;
        }
    }

    /**
     * Recommends tutors using a Max-Heap (PriorityQueue) based on composite scores.
     */
    public List<ScoredTutor> recommendTutors(List<TutorProfile> tutors, Request request) {
        List<ScoredTutor> results = new ArrayList<>();
        if (tutors == null || tutors.isEmpty()) return results;

        // Custom Comparator for Max-Heap (Highest score first)
        // DSA Heap Concept: PriorityQueue implements a balanced binary heap
        PriorityQueue<ScoredTutor> maxHeap = new PriorityQueue<>(
            (a, b) -> Double.compare(b.getScore(), a.getScore())
        );

        // Calculate scores and insert into Heap
        for (TutorProfile tutor : tutors) {
            double score = calculateCompositeScore(tutor, request);
            maxHeap.offer(new ScoredTutor(tutor, score));
        }

        // Poll top K elements from the Heap
        int count = 0;
        while (!maxHeap.isEmpty() && count < request.limit) {
            results.add(maxHeap.poll());
            count++;
        }

        return results;
    }

    /**
     * Composite scoring system showcasing the Strategy Pattern usage via the Levenshtein class.
     */
    private double calculateCompositeScore(TutorProfile tutor, Request request) {
        double locationScore = 0.0;
        double bioScore = 0.0;
        double subjectScore = 0.0;

        // 1. Location match using contains checking & Levenshtein distance
        if (request.preferredLocation != null && !request.preferredLocation.isEmpty() && tutor.getLocation() != null) {
            String tutorLoc = tutor.getLocation().toLowerCase().trim();
            String prefLoc = request.preferredLocation.toLowerCase().trim();
            if (tutorLoc.contains(prefLoc) || prefLoc.contains(tutorLoc)) {
                locationScore = 1.0;
            } else {
                locationScore = levenshteinMatchStrategy.calculateSimilarity(tutor.getLocation(), request.preferredLocation);
            }
        }

        // 2. Subject relevance match using contains checking & Levenshtein distance
        if (request.subjectKeyword != null && !request.subjectKeyword.isEmpty() && tutor.getSubjects() != null) {
            String targetSub = request.subjectKeyword.toLowerCase().trim();
            for (var subject : tutor.getSubjects()) {
                String subName = subject.getName().toLowerCase().trim();
                double similarity;
                if (subName.contains(targetSub) || targetSub.contains(subName)) {
                    similarity = 1.0;
                } else {
                    similarity = levenshteinMatchStrategy.calculateSimilarity(subject.getName(), request.subjectKeyword);
                }
                if (similarity > subjectScore) {
                    subjectScore = similarity;
                }
            }
        }

        // 3. Biography match (check if biography contains keyword or matches context)
        if (request.searchBioKeyword != null && !request.searchBioKeyword.isEmpty() && tutor.getBio() != null) {
            String bioLower = tutor.getBio().toLowerCase();
            String keywordLower = request.searchBioKeyword.toLowerCase();
            if (bioLower.contains(keywordLower)) {
                bioScore = 1.0;
            } else {
                // Fuzzy match check of the bio for the search word
                bioScore = levenshteinMatchStrategy.calculateSimilarity(tutor.getBio(), request.searchBioKeyword) * 0.3;
            }
        }

        // OOP Concept: Factory logic mapping modes to weights
        switch (request.mode.toUpperCase()) {
            case "LOCATION":
                return (locationScore * 0.8) + (subjectScore * 0.2);
            case "BIO":
                return (bioScore * 0.8) + (subjectScore * 0.2);
            case "HYBRID":
            default:
                return (locationScore * 0.4) + (subjectScore * 0.4) + (bioScore * 0.2);
        }
    }
}
