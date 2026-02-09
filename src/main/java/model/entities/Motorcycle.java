package model.entities;

import java.time.LocalDateTime;

/**
 * Entity representing a Motorcycle vehicle.
 * Implements specific fee calculation and engine displacement properties.
 */
public class Motorcycle extends Vehicle {

    private static final double HOURLY_RATE = 400.0;
    private int cylinderCapacity;

    public Motorcycle() {
        super();
    }

    public Motorcycle(int cylinderCapacity, String licensePlate, String brand, String model, LocalDateTime entryTime) {
        super(licensePlate, brand, model, entryTime);
        this.cylinderCapacity = cylinderCapacity;
    }

    /**
     * Calculates the parking fee based on the motorcycle's hourly rate.
     * @param hours Duration of stay in hours.
     * @return Total fee calculation.
     */
    @Override
    public double calculateFee(long hours) {
        return hours * HOURLY_RATE;
    }

    // Getters and Setters
    public int getCylinderCapacity() {
        return cylinderCapacity;
    }

    public void setCylinderCapacity(int cylinderCapacity) {
        this.cylinderCapacity = cylinderCapacity;
    }
}