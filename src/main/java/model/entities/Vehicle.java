package model.entities;

import java.time.LocalDateTime;

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

    public abstract double calculateFee(long hours);
}
