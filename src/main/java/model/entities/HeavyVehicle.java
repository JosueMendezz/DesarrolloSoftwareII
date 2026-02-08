package model.entities;

import java.time.LocalDateTime;

public class HeavyVehicle extends Vehicle {

    public HeavyVehicle(double maxLoadWeight, String licensePlate, String brand, String model, LocalDateTime entryTime) {
        super(licensePlate, brand, model, entryTime);
    }

    @Override
    public double calculateFee(long hours) {
        double hourlyRate = 1500.0;
        return hours * hourlyRate;
    }
}
