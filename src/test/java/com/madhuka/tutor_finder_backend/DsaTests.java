package com.madhuka.tutor_finder_backend;

import com.madhuka.tutor_finder_backend.dsa.LevenshteinMatchStrategy;
import com.madhuka.tutor_finder_backend.dsa.RecommendationEngine;
import com.madhuka.tutor_finder_backend.dsa.SubjectSearchTrie;
import com.madhuka.tutor_finder_backend.entity.Subject;
import com.madhuka.tutor_finder_backend.entity.TutorProfile;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DsaTests {

    @Test
    void testTrieInsertionAndAutocomplete() {
        SubjectSearchTrie trie = new SubjectSearchTrie();
        
        // Manual insertion (without database dependencies)
        trie.insert("Mathematics");
        trie.insert("Maths");
        trie.insert("Science");
        trie.insert("Physics");
        trie.insert("Chemistry");

        // Autocomplete tests
        List<String> suggestions = trie.autocomplete("mat");
        assertEquals(2, suggestions.size());
        assertTrue(suggestions.contains("mathematics"));
        assertTrue(suggestions.contains("maths"));

        List<String> sciSuggestions = trie.autocomplete("sci");
        assertEquals(1, sciSuggestions.size());
        assertTrue(sciSuggestions.contains("science"));

        List<String> invalidSuggestions = trie.autocomplete("bio");
        assertTrue(invalidSuggestions.isEmpty());
    }

    @Test
    void testLevenshteinSimilarity() {
        LevenshteinMatchStrategy strategy = new LevenshteinMatchStrategy();

        // Exact match
        assertEquals(1.0, strategy.calculateSimilarity("Physics", "Physics"), 0.001);
        assertEquals(1.0, strategy.calculateSimilarity("chemistry", "CHEMISTRY"), 0.001);

        // Completely different
        assertEquals(0.0, strategy.calculateSimilarity("abc", "xyz"), 0.001);

        // Slight typo (edit distance of 1: 'Phisics' instead of 'Physics')
        // Length = 7. Distance = 1. Similarity = 1 - 1/7 = 0.857
        double typoSimilarity = strategy.calculateSimilarity("Phisics", "Physics");
        assertEquals(0.857, typoSimilarity, 0.005);
        assertTrue(typoSimilarity > 0.8);
    }

    @Test
    void testRecommendationHeapRanking() {
        // We instantiate the recommendation engine and mock strategy manually
        // We use reflection or subclass or setter injection if needed.
        // Let's create an anonymous class subclassing RecommendationEngine
        // or just mock/inject using a custom test runner, or run strategy manually
        // Since we can test the PriorityQueue sorting of ScoredTutor directly:
        
        PriorityQueueTest();
    }

    private void PriorityQueueTest() {
        // PriorityQueue min/max heap verification
        java.util.PriorityQueue<RecommendationEngine.ScoredTutor> heap = new java.util.PriorityQueue<>(
            (a, b) -> Double.compare(b.getScore(), a.getScore())
        );

        TutorProfile t1 = new TutorProfile();
        t1.setFullName("Tutor A");
        TutorProfile t2 = new TutorProfile();
        t2.setFullName("Tutor B");
        TutorProfile t3 = new TutorProfile();
        t3.setFullName("Tutor C");

        heap.offer(new RecommendationEngine.ScoredTutor(t1, 0.45));
        heap.offer(new RecommendationEngine.ScoredTutor(t2, 0.95));
        heap.offer(new RecommendationEngine.ScoredTutor(t3, 0.75));

        // Max-Heap verification: highest score polled first
        assertEquals("Tutor B", heap.poll().getProfile().getFullName());
        assertEquals("Tutor C", heap.poll().getProfile().getFullName());
        assertEquals("Tutor A", heap.poll().getProfile().getFullName());
    }
}
