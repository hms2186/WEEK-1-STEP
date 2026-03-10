import java.util.*;

public class PlagiarismDetector {
    // Inverted Index: N-gram -> Set of Document IDs that contain it
    private Map<String, Set<String>> ngramIndex = new HashMap<>();
    private final int N = 5; // We use 5-grams

    /**
     * Adds a document to the database.
     */
    public void addDocument(String docId, String content) {
        List<String> ngrams = extractNgrams(content);
        for (String gram : ngrams) {
            ngramIndex.putIfAbsent(gram, new HashSet<>());
            ngramIndex.get(gram).add(docId);
        }
    }

    /**
     * Analyzes a new document against the database.
     */
    public void analyzeDocument(String newDocContent) {
        List<String> ngrams = extractNgrams(newDocContent);
        int totalNgrams = ngrams.size();
        
        // Map to track: docId -> Count of matching n-grams
        Map<String, Integer> matchCounts = new HashMap<>();

        for (String gram : ngrams) {
            if (ngramIndex.containsKey(gram)) {
                for (String docId : ngramIndex.get(gram)) {
                    matchCounts.put(docId, matchCounts.getOrDefault(docId, 0) + 1);
                }
            }
        }

        // Calculate and report results
        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {
            double similarity = (entry.getValue() * 100.0) / totalNgrams;
            System.out.printf("Match with %s: %.1f%% similarity\n", entry.getKey(), similarity);
            
            if (similarity > 50.0) System.out.println("ALERT: PLAGIARISM DETECTED!");
        }
    }

    /**
     * Helper to break text into N-grams.
     */
    private List<String> extractNgrams(String text) {
        String[] words = text.toLowerCase().split("\\s+");
        List<String> ngrams = new ArrayList<>();
        
        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < N; j++) {
                sb.append(words[i + j]).append(" ");
            }
            ngrams.add(sb.toString().trim());
        }
        return ngrams;
    }

    public static void main(String[] args) {
        PlagiarismDetector pd = new PlagiarismDetector();
        pd.addDocument("Essay_092", "the quick brown fox jumps over the lazy dog");
        
        String studentSubmission = "the quick brown fox jumps over a sleeping dog";
        pd.analyzeDocument(studentSubmission);
    }
}
