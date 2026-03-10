import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class FlashSaleManager {
    // productId -> currentStock (Thread-safe map)
    private final Map<String, Integer> inventory = new ConcurrentHashMap<>();
    
    // productId -> Queue of UserIDs (Maintains FIFO order for waiting list)
    private final Map<String, Queue<Integer>> waitingLists = new ConcurrentHashMap<>();

    public FlashSaleManager() {
        // Initialize stock for the flash sale
        inventory.put("IPHONE15_256GB", 100);
    }

    /**
     * Checks stock in O(1) time.
     */
    public int checkStock(String productId) {
        return inventory.getOrDefault(productId, 0);
    }

    /**
     * Handles the purchase logic with synchronization to prevent overselling.
     */
    public synchronized String purchaseItem(String productId, int userId) {
        int currentStock = checkStock(productId);

        if (currentStock > 0) {
            // Deduct stock and process success
            inventory.put(productId, currentStock - 1);
            return "Success! Remaining: " + (currentStock - 1);
        } else {
            // Stock is 0, add to waiting list (FIFO)
            waitingLists.putIfAbsent(productId, new LinkedBlockingQueue<>());
            Queue<Integer> waitList = waitingLists.get(productId);
            
            if (!waitList.contains(userId)) {
                waitList.add(userId);
            }
            
            return "Sold Out. Added to waiting list at position #" + waitList.size();
        }
    }

    public static void main(String[] args) {
        FlashSaleManager sale = new FlashSaleManager();

        // Simulate purchases
        System.out.println(sale.purchaseItem("IPHONE15_256GB", 12345)); // Success
        
        // Mocking stock depletion for demo
        sale.inventory.put("IPHONE15_256GB", 0);
        
        System.out.println(sale.purchaseItem("IPHONE15_256GB", 99999)); // Waiting list
        System.out.println(sale.purchaseItem("IPHONE15_256GB", 88888)); // Waiting list
    }
}
