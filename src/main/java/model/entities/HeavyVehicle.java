package model.entities;

import java.time.LocalDateTime;

/**
 * Entity representing a Heavy Vehicle (Trucks, Buses, etc.).
 * Implements specific fee calculation and weight properties.
 */
public class HeavyVehicle extends Vehicle {

    private static final double HOURLY_RATE = 1500.0;
    private double maxLoadWeight;

    public HeavyVehicle() {
        super();
    }

    public HeavyVehicle(double maxLoadWeight, String licensePlate, String brand, String model, LocalDateTime entryTime) {
        super(licensePlate, brand, model, entryTime);
        this.maxLoadWeight = maxLoadWeight;
    }

    /**
     * Calculates the parking fee based on the heavy vehicle's hourly rate.
     * @param hours Duration of stay in hours.
     * @return Total fee calculation.
     */
    @Override
    public double calculateFee(long hours) {
        return hours * HOURLY_RATE;
    }

    // Getters and Setters
    public double getMaxLoadWeight() {
        return maxLoadWeight;
    }

    public void setMaxLoadWeight(double maxLoadWeight) {
        this.maxLoadWeight = maxLoadWeight;
    }
}