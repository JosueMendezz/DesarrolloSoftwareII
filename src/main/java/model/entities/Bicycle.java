package model.entities;

import java.time.LocalDateTime;

public class Bicycle extends Vehicle {

    public Bicycle(String licensePlate, String brand, String model, LocalDateTime entryTime) {
        super(licensePlate, brand, model, entryTime);
    }

    public Bicycle() {
    }

    @Override
    public double calculateFee(long hours) {
        double hourlyRate = 200.0;
        return hours * hourlyRate;
    }
}