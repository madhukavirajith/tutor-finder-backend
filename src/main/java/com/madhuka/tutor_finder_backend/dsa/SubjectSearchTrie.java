package com.madhuka.tutor_finder_backend.dsa;

import com.madhuka.tutor_finder_backend.entity.Subject;
import com.madhuka.tutor_finder_backend.repository.SubjectRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ============================================================================
 * INTERVIEW TOPIC: TRIE DATA STRUCTURE, DFS RECURSION, & SINGLETON PATTERN
 * ============================================================================
 * 1. SINGLETON DESIGN PATTERN: Spring's @Component registers this class as a
 *    singleton bean. All requests share the same prefix-tree instance in memory.
 * 2. ALGORITHMS:
 *    - Insertion: O(L) time complexity where L is the length of the string.
 *    - Autocomplete:
 *      a) Traverse down to the end node representing the prefix: O(P) where P is prefix length.
 *      b) Perform Depth-First Search (DFS) / Pre-order traversal to fetch all matching
 *         words under that branch: O(N) where N is the number of nodes in the sub-tree.
 * ============================================================================
 */
@Component
public class SubjectSearchTrie {

    private final TrieNode root;

    @Autowired
    private SubjectRepository subjectRepository;

    public SubjectSearchTrie() {
        this.root = new TrieNode();
    }

    /**
     * PostConstruct lifecycle hook: Loads existing subjects from the database
     * on startup and populates the Trie.
     */
    @PostConstruct
    public void initializeTrie() {
        if (subjectRepository.count() == 0) {
            List<String> defaultSubjects = List.of(
                "Mathematics", "Science", "English", "History", "Physics", 
                "Chemistry", "Biology", "Computer Science", "Geography", "Art"
            );
            for (String name : defaultSubjects) {
                Subject sub = new Subject();
                sub.setName(name);
                subjectRepository.save(sub);
            }
        }
        List<Subject> subjects = subjectRepository.findAll();
        for (Subject subject : subjects) {
            insert(subject.getName());
        }
        System.out.println(">>> SubjectSearchTrie initialized with " + subjects.size() + " subjects!");
    }

    /**
     * Inserts a word (subject) into the Trie.
     * Time Complexity: O(L) where L = word length.
     * Space Complexity: O(L) in the worst case (if characters are new).
     */
    public void insert(String word) {
        if (word == null || word.trim().isEmpty()) return;

        TrieNode current = root;
        // Convert to lowercase for case-insensitive searching
        String normalizedWord = word.trim().toLowerCase();

        for (int i = 0; i < normalizedWord.length(); i++) {
            char ch = normalizedWord.charAt(i);
            if (!current.hasChild(ch)) {
                current.putChild(ch, new TrieNode());
            }
            current = current.getChild(ch);
        }
        current.setEndOfWord(true);
    }

    /**
     * Returns a list of all words matching the given prefix.
     * Time Complexity: O(P + V) where P is prefix length and V is visited nodes in the sub-tree.
     */
    public List<String> autocomplete(String prefix) {
        List<String> results = new ArrayList<>();
        if (prefix == null || prefix.trim().isEmpty()) return results;

        String normalizedPrefix = prefix.trim().toLowerCase();
        TrieNode current = root;

        // 1. Navigate to the last node of the prefix
        for (int i = 0; i < normalizedPrefix.length(); i++) {
            char ch = normalizedPrefix.charAt(i);
            if (!current.hasChild(ch)) {
                return results; // Prefix not found, return empty results
            }
            current = current.getChild(ch);
        }

        // 2. Perform DFS recursion from this node to find all complete words
        dfsCollect(current, new StringBuilder(normalizedPrefix), results);
        return results;
    }

    /**
     * Depth-First Search (DFS) helper method.
     * Uses Recursion to traverse branches and collect complete words.
     */
    private void dfsCollect(TrieNode node, StringBuilder currentWord, List<String> results) {
        if (node.isEndOfWord()) {
            results.add(currentWord.toString());
        }

        // Traverse all children of the current node
        for (Map.Entry<Character, TrieNode> entry : node.getChildren().entrySet()) {
            char ch = entry.getKey();
            TrieNode childNode = entry.getValue();

            // Append character, recurse, then backtrack
            currentWord.append(ch);
            dfsCollect(childNode, currentWord, results);
            currentWord.deleteCharAt(currentWord.length() - 1); // Backtracking
        }
    }
}
