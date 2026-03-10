import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AnalyticsSystem {
    // 1. Total Page Views
    private final Map<String, Integer> pageViews = new ConcurrentHashMap<>();
    
    // 2. Unique Visitors (Set handles duplicates automatically)
    private final Map<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();
    
    // 3. Traffic Sources
    private final Map<String, Integer> trafficSources = new ConcurrentHashMap<>();

    /**
     * Processes a single page view event in O(1) average time.
     */
    public void processEvent(String url, String userId, String source) {
        // Update Total Views
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);

        // Update Unique Visitors
        uniqueVisitors.putIfAbsent(url, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        uniqueVisitors.get(url).add(userId);

        // Update Traffic Source
        trafficSources.put(source, trafficSources.getOrDefault(source, 0) + 1);
    }

    /**
     * Returns the top N most visited pages.
     */
    public List<Map.Entry<String, Integer>> getTopPages(int n) {
        return pageViews.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    public void displayDashboard() {
        System.out.println("--- REAL-TIME DASHBOARD ---");
        System.out.println("Top 3 Pages:");
        getTopPages(3).forEach(entry -> {
            int total = entry.getValue();
            int unique = uniqueVisitors.get(entry.getKey()).size();
            System.out.println(entry.getKey() + " - " + total + " views (" + unique + " unique)");
        });

        System.out.println("\nTraffic Sources: " + trafficSources);
    }

    public static void main(String[] args) {
        AnalyticsSystem dashboard = new AnalyticsSystem();

        // Simulating incoming traffic
        dashboard.processEvent("/news", "user1", "Google");
        dashboard.processEvent("/news", "user2", "Facebook");
        dashboard.processEvent("/news", "user1", "Google"); // Same user, different view
        dashboard.processEvent("/sports", "user3", "Direct");

        dashboard.displayDashboard();
    }
}
