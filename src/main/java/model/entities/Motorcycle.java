package model.entities;

import java.time.LocalDateTime;

public class Motorcycle extends Vehicle {

    public Motorcycle(int cylinderCapacity, String licensePlate, String brand, String model, LocalDateTime entryTime) {
        super(licensePlate, brand, model, entryTime);
    }

    public Motorcycle() {
    }

    @Override
    public double calculateFee(long hours) {
        double hourlyRate = 400.0; 
        return hours * hourlyRate;
    }
}
