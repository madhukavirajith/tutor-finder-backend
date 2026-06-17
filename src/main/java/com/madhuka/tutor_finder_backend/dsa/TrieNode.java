package com.madhuka.tutor_finder_backend.dsa;

import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================================
 * INTERVIEW TOPIC: ENCAPSULATION & DATA STRUCTURES (Trie Node)
 * ============================================================================
 * 1. ENCAPSULATION: We declare fields (children, isEndOfWord) as private and
 *    expose controlled helper methods (putChild, getChild, etc.) to read/write them.
 * 2. DATA STRUCTURES: A Trie node holds a dictionary (HashMap) representing pointers
 *    to child nodes. HashMaps offer O(1) average time complexity for character lookups.
 * ============================================================================
 */
public class TrieNode {
    // Encapsulation: Keep the map of children private
    private final Map<Character, TrieNode> children;
    
    // Encapsulation: Keep the end-of-word marker private
    private boolean isEndOfWord;

    public TrieNode() {
        this.children = new HashMap<>();
        this.isEndOfWord = false;
    }

    // --- ENCAPSULATED METHODS / BEHAVIORS ---

    public boolean hasChild(char ch) {
        return children.containsKey(ch);
    }

    public void putChild(char ch, TrieNode node) {
        children.put(ch, node);
    }

    public TrieNode getChild(char ch) {
        return children.get(ch);
    }

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public boolean isEndOfWord() {
        return isEndOfWord;
    }

    public void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }
}
