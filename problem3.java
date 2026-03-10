import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DNSCache {
    // Inner class to store DNS details
    class DNSEntry {
        String ipAddress;
        long expiryTime; // System time in ms when this expires

        public DNSEntry(String ipAddress, int ttlSeconds) {
            this.ipAddress = ipAddress;
            // Current time + (TTL in seconds converted to milliseconds)
            this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000L);
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    private final Map<String, DNSEntry> cache = new ConcurrentHashMap<>();
    private int hits = 0;
    private int misses = 0;

    /**
     * Resolves domain to IP. 
     * Handles Hits, Misses, and Expirations.
     */
    public String resolve(String domain) {
        DNSEntry entry = cache.get(domain);

        if (entry != null && !entry.isExpired()) {
            hits++;
            return "Cache HIT -> " + entry.ipAddress;
        }

        // Cache Miss or Expired
        misses++;
        if (entry != null) cache.remove(domain); // Clean up expired entry
        
        // Simulate Upstream DNS Query
        String resolvedIp = queryUpstream(domain);
        cache.put(domain, new DNSEntry(resolvedIp, 300)); // 300s TTL
        return "Cache MISS -> Query Upstream -> " + resolvedIp;
    }

    private String queryUpstream(String domain) {
        return "172.217.14." + (int)(Math.random() * 255);
    }

    public void getCacheStats() {
        double total = hits + misses;
        double hitRate = (total == 0) ? 0 : (hits / total) * 100;
        System.out.println("Stats - Hits: " + hits + ", Misses: " + misses + ", Hit Rate: " + hitRate + "%");
    }

    public static void main(String[] args) throws InterruptedException {
        DNSCache dns = new DNSCache();
        
        System.out.println(dns.resolve("google.com")); // MISS
        System.out.println(dns.resolve("google.com")); // HIT
        
        dns.getCacheStats();
    }
}
