package model.entities;

import java.time.LocalDateTime;

/**
 * Entity representing a Bicycle vehicle.
 * Implements specific fee calculation for bicycles.
 */
public class Bicycle extends Vehicle {

    private static final double HOURLY_RATE = 200.0;

    public Bicycle() {
        super();
    }

    public Bicycle(String licensePlate, String brand, String model, LocalDateTime entryTime) {
        super(licensePlate, brand, model, entryTime);
    }

    /**
     * Calculates the parking fee based on the bicycle's hourly rate.
     * @param hours Duration of stay in hours.
     * @return Total fee calculation.
     */
    @Override
    public double calculateFee(long hours) {
        return hours * HOURLY_RATE;
    }
}