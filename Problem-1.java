import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UsernameSystem {
    // Stores existing usernames for O(1) lookup
    // Using ConcurrentHashMap for thread-safety with 1000+ concurrent requests
    private final Map<String, Integer> userRegistry = new ConcurrentHashMap<>();
    
    // Tracks how many times a username was attempted
    private final Map<String, Integer> attemptTracker = new ConcurrentHashMap<>();

    public UsernameSystem() {
        // Pre-populating some data for the demo
        userRegistry.put("john_doe", 101);
        userRegistry.put("jane_doe", 102);
        userRegistry.put("admin", 1);
    }

    /**
     * Checks if a username is available in O(1) time.
     */
    public boolean checkAvailability(String username) {
        String lowerCaseName = username.toLowerCase();
        
        // Track the attempt frequency
        attemptTracker.put(lowerCaseName, attemptTracker.getOrDefault(lowerCaseName, 0) + 1);
        
        // Return true if it does NOT exist in our registry
        return !userRegistry.containsKey(lowerCaseName);
    }

    /**
     * Generates similar available usernames.
     */
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int suffix = 1;

        while (suggestions.size() < 3) {
            String candidate = username + suffix;
            if (!userRegistry.containsKey(candidate)) {
                suggestions.add(candidate);
            }
            suffix++;
        }
        
        // Adding a variation with a dot
        String dotVariation = username.contains("_") ? username.replace("_", ".") : username + ".official";
        if (!userRegistry.containsKey(dotVariation)) {
            suggestions.add(dotVariation);
        }

        return suggestions;
    }

    /**
     * Finds the most searched username.
     */
    public String getMostAttempted() {
        return attemptTracker.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No attempts yet");
    }

    public static void main(String[] args) {
        UsernameSystem sys = new UsernameSystem();

        // 1. Check Availability
        System.out.println("Is 'john_doe' available? " + sys.checkAvailability("john_doe"));
        System.out.println("Is 'jane_smith' available? " + sys.checkAvailability("jane_smith"));

        // 2. Get Suggestions
        if (!sys.checkAvailability("john_doe")) {
            System.out.println("Suggestions for 'john_doe': " + sys.suggestAlternatives("john_doe"));
        }

        // 3. Track Popularity
        sys.checkAvailability("admin");
        sys.checkAvailability("admin");
        System.out.println("Most attempted username: " + sys.getMostAttempted());
    }
}
