import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {
    // Inner class to represent a user's specific "bucket"
    class TokenBucket {
        long tokens;
        long lastRefillTime;
        final long MAX_TOKENS = 1000;
        final long REFILL_INTERVAL_MS = 3600000; // 1 hour

        public TokenBucket() {
            this.tokens = MAX_TOKENS;
            this.lastRefillTime = System.currentTimeMillis();
        }

        // Lazily refill tokens based on time passed
        public synchronized boolean allowRequest() {
            long now = System.currentTimeMillis();
            
            // If an hour has passed since the last refill, reset tokens
            if (now - lastRefillTime > REFILL_INTERVAL_MS) {
                tokens = MAX_TOKENS;
                lastRefillTime = now;
            }

            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }
    }

    private final Map<String, TokenBucket> clientBuckets = new ConcurrentHashMap<>();

    public String checkRateLimit(String clientId) {
        // ComputeIfAbsent is O(1) and thread-safe
        TokenBucket bucket = clientBuckets.computeIfAbsent(clientId, k -> new TokenBucket());

        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.tokens + " requests remaining)";
        } else {
            long waitTime = (bucket.lastRefillTime + bucket.REFILL_INTERVAL_MS - System.currentTimeMillis()) / 1000;
            return "Denied (Retry after " + waitTime + " seconds)";
        }
    }

    public static void main(String[] args) {
        RateLimiter limiter = new RateLimiter();
        
        // Simulating requests
        System.out.println(limiter.checkRateLimit("user_1")); // Allowed
        System.out.println(limiter.checkRateLimit("user_1")); // Allowed
    }
}
