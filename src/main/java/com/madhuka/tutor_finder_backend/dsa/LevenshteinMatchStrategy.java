package com.madhuka.tutor_finder_backend.dsa;

import org.springframework.stereotype.Component;

/**
 * ============================================================================
 * INTERVIEW TOPIC: DYNAMIC PROGRAMMING (Levenshtein Distance)
 * ============================================================================
 * 1. DYNAMIC PROGRAMMING: We solve the edit distance problem by breaking it down
 *    into smaller subproblems. We build a bottom-up 2D grid 'dp' of size (M+1) x (N+1)
 *    where dp[i][j] represents the minimum operations to convert prefix s1[0...i-1]
 *    to prefix s2[0...j-1].
 * 
 * 2. RECURRENCE RELATION:
 *    - If s1.charAt(i-1) == s2.charAt(j-1): dp[i][j] = dp[i-1][j-1] (no operation needed)
 *    - Else: dp[i][j] = 1 + min(
 *                           dp[i-1][j],   // Deletion
 *                           dp[i][j-1],   // Insertion
 *                           dp[i-1][j-1]  // Substitution
 *                        )
 * 
 * 3. COMPLEXITY:
 *    - Time Complexity: O(M * N) where M and N are lengths of s1 and s2.
 *    - Space Complexity: O(M * N) to store the DP table.
 * ============================================================================
 */
@Component
public class LevenshteinMatchStrategy implements MatchStrategy {

    @Override
    public double calculateSimilarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0.0;

        String str1 = s1.trim().toLowerCase();
        String str2 = s2.trim().toLowerCase();

        if (str1.equals(str2)) return 1.0;
        if (str1.isEmpty() || str2.isEmpty()) return 0.0;

        int m = str1.length();
        int n = str2.length();

        // DP table allocation
        int[][] dp = new int[m + 1][n + 1];

        // Base cases: converting empty string to string of length i/j
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }

        // Fill the DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // Characters match, no operation
                } else {
                    dp[i][j] = 1 + Math.min(
                            dp[i - 1][j], // Deletion
                            Math.min(
                                    dp[i][j - 1],   // Insertion
                                    dp[i - 1][j - 1] // Substitution
                            )
                    );
                }
            }
        }

        // Levenshtein edit distance
        int editDistance = dp[m][n];

        // Convert edit distance to a similarity ratio [0.0 - 1.0]
        int maxLength = Math.max(m, n);
        return 1.0 - ((double) editDistance / maxLength);
    }
}
