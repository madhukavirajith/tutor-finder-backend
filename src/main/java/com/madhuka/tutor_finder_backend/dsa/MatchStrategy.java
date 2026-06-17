package com.madhuka.tutor_finder_backend.dsa;

/**
 * ============================================================================
 * INTERVIEW TOPIC: STRATEGY DESIGN PATTERN (OOP Abstraction)
 * ============================================================================
 * 1. STRATEGY DESIGN PATTERN: We declare a generic interface interface MatchStrategy.
 *    Any algorithm for comparing string matches (e.g. Exact Match, Levenshtein Distance,
 *    Jaro-Winkler) can implement this interface and be swapped dynamically at runtime.
 * ============================================================================
 */
public interface MatchStrategy {
    /**
     * Compares two strings and returns a similarity score between 0.0 (no similarity)
     * and 1.0 (identical strings).
     */
    double calculateSimilarity(String s1, String s2);
}
