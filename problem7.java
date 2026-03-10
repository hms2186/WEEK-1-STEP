import java.util.*;

public class AutocompleteSystem {
    // Standard Trie Node
    class TrieNode {
        // Map of character -> next node (Handles the branching)
        Map<Character, TrieNode> children = new HashMap<>();
        // Map of query -> frequency (Only stored at the end of a word)
        Map<String, Integer> counts = new HashMap<>();
        // Optional: List<String> top10Cache; // For O(1) suggestions
    }

    private final TrieNode root = new TrieNode();

    /**
     * Updates the frequency of a search query.
     */
    public void updateFrequency(String query) {
        TrieNode current = root;
        for (char c : query.toCharArray()) {
            current.children.putIfAbsent(c, new TrieNode());
            current = current.children.get(c);
        }
        // At the leaf node, update the count
        current.counts.put(query, current.counts.getOrDefault(query, 0) + 1);
    }

    /**
     * Returns suggestions for a given prefix.
     */
    public List<String> getSuggestions(String prefix) {
        TrieNode current = root;
        // 1. Navigate to the end of the prefix
        for (char c : prefix.toCharArray()) {
            if (!current.children.containsKey(c)) return new ArrayList<>();
            current = current.children.get(c);
        }

        // 2. Collect all queries under this node (DFS)
        Map<String, Integer> results = current.counts;
        
        // 3. Sort by frequency and return top 10
        return results.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(10)
                .map(Map.Entry::getKey)
                .toList();
    }

    public static void main(String[] args) {
        AutocompleteSystem ac = new AutocompleteSystem();
        ac.updateFrequency("java tutorial");
        ac.updateFrequency("java tutorial"); // Count = 2
        ac.updateFrequency("javascript");
        ac.updateFrequency("java download");

        System.out.println("Suggestions for 'jav': " + ac.getSuggestions("jav"));
    }
}
