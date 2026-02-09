package model.entities;

import java.time.LocalDateTime;

/**
 * Abstract base class representing a generic Vehicle.
 * Provides shared attributes and enforces fee calculation for subclasses.
 */
public abstract class Vehicle {

    private String licensePlate;
    private String brand;
    private String model;
    private LocalDateTime entryTime;

    public Vehicle() {
    }

    public Vehicle(String licensePlate, String brand, String model, LocalDateTime entryTime) {
        this.licensePlate = licensePlate;
        this.brand = brand;
        this.model = model;
        this.entryTime = entryTime;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    /**
     * Abstract method to be implemented by specific vehicle types.
     * @param hours Total hours spent in the parking lot.
     * @return The calculated fee based on the vehicle type's rate.
     */
    public abstract double calculateFee(long hours);
}