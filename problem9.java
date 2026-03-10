import java.util.*;

public class FraudDetector {
    
    class Transaction {
        int id;
        int amount;
        String merchant;
        long timestamp; // ms

        Transaction(int id, int amount, String merchant) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Classic Two-Sum using HashMap.
     * Finds pairs that sum exactly to target.
     */
    public List<String> findTwoSum(List<Transaction> txns, int target) {
        // Map: Amount -> Transaction ID
        Map<Integer, Integer> map = new HashMap<>();
        List<String> pairs = new ArrayList<>();

        for (Transaction t : txns) {
            int complement = target - t.amount;
            
            if (map.containsKey(complement)) {
                pairs.add("Pair Found: ID " + t.id + " and ID " + map.get(complement));
            }
            // Store current amount so future transactions can find it
            map.put(t.amount, t.id);
        }
        return pairs;
    }
}
