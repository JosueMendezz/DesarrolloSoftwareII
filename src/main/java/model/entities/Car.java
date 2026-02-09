package model.entities;

import java.time.LocalDateTime;

/**
 * Entity representing a Car vehicle.
 * Implements specific fee calculation and properties for cars.
 */
public class Car extends Vehicle {

    private static final double HOURLY_RATE = 800.0;
    private int numberOfDoors;

    public Car() {
        super();
    }

    public Car(int numberOfDoors, String licensePlate, String brand, String model, LocalDateTime entryTime) {
        super(licensePlate, brand, model, entryTime);
        this.numberOfDoors = numberOfDoors;
    }

    /**
     * Calculates the parking fee based on the car's hourly rate.
     * @param hours Duration of stay in hours.
     * @return Total fee calculation.
     */
    @Override
    public double calculateFee(long hours) {
        return hours * HOURLY_RATE;
    }

    // Getters and Setters
    public int getNumberOfDoors() {
        return numberOfDoors;
    }

    public void setNumberOfDoors(int numberOfDoors) {
        this.numberOfDoors = numberOfDoors;
    }
}