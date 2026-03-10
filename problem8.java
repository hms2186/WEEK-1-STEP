public class ParkingLot {
    enum Status { EMPTY, OCCUPIED, DELETED }

    class Spot {
        String licensePlate;
        long entryTime;
        Status status = Status.EMPTY;

        Spot(String lp) {
            this.licensePlate = lp;
            this.entryTime = System.currentTimeMillis();
            this.status = Status.OCCUPIED;
        }
    }

    private Spot[] spots;
    private int capacity;
    private int occupiedCount = 0;

    public ParkingLot(int size) {
        this.capacity = size;
        this.spots = new Spot[size];
    }

    // Simple hash function
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    /**
     * Finds a spot using Linear Probing.
     */
    public int parkVehicle(String licensePlate) {
        if (occupiedCount >= capacity) return -1; // Full

        int index = hash(licensePlate);
        int probes = 0;

        // Linear Probing: Find the first EMPTY or DELETED spot
        while (spots[index] != null && spots[index].status == Status.OCCUPIED) {
            index = (index + 1) % capacity;
            probes++;
        }

        spots[index] = new Spot(licensePlate);
        occupiedCount++;
        System.out.println("Parked " + licensePlate + " at #" + index + " (" + probes + " probes)");
        return index;
    }

    /**
     * Finds the vehicle and frees the spot.
     */
    public void exitVehicle(String licensePlate) {
        int index = hash(licensePlate);
        int start = index;

        while (spots[index] != null) {
            if (spots[index].status == Status.OCCUPIED && spots[index].licensePlate.equals(licensePlate)) {
                spots[index].status = Status.DELETED; // Use DELETED, not EMPTY
                occupiedCount--;
                System.out.println("Vehicle " + licensePlate + " exited spot #" + index);
                return;
            }
            index = (index + 1) % capacity;
            if (index == start) break; // Traversed whole array
        }
        System.out.println("Vehicle not found.");
    }
}
