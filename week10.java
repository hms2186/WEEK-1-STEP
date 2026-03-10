import java.util.*;

public class MultiLevelCache {
    // L1 Cache with LRU logic (10,000 limit)
    private final int L1_SIZE = 10000;
    private LinkedHashMap<String, String> l1Cache = new LinkedHashMap<>(L1_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > L1_SIZE; // Automatically evicts oldest
        }
    };

    // L2 Cache (SSD-backed simulation)
    private Map<String, String> l2Cache = new HashMap<>();
    
    // Statistics
    private int l1Hits = 0, l2Hits = 0, l3Hits = 0, totalRequests = 0;

    public String getVideo(String videoId) {
        totalRequests++;
        
        // 1. Check L1
        if (l1Cache.containsKey(videoId)) {
            l1Hits++;
            return l1Cache.get(videoId) + " (L1 HIT)";
        }

        // 2. Check L2
        if (l2Cache.containsKey(videoId)) {
            l2Hits++;
            String data = l2Cache.get(videoId);
            promoteToL1(videoId, data);
            return data + " (L2 HIT - Promoted to L1)";
        }

        // 3. Check L3 (Database)
        l3Hits++;
        String data = queryDatabase(videoId);
        addToL2(videoId, data);
        return data + " (L3 HIT - Added to L2)";
    }

    private void promoteToL1(String id, String data) {
        l1Cache.put(id, data);
    }

    private void addToL2(String id, String data) {
        l2Cache.put(id, data);
    }

    private String queryDatabase(String id) {
        return "VideoData_" + id;
    }

    public void printStats() {
        System.out.println("L1 Hit Rate: " + (l1Hits * 100.0 / totalRequests) + "%");
        System.out.println("L2 Hit Rate: " + (l2Hits * 100.0 / totalRequests) + "%");
        System.out.println("Overall Hit Rate: " + ((l1Hits + l2Hits) * 100.0 / totalRequests) + "%");
    }
}
