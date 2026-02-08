package model.entities;

import java.time.LocalDateTime;

public class Car extends Vehicle {

    public Car() {
    }

    public Car(int numberOfDoors, String licensePlate, String brand, String model, LocalDateTime entryTime) {
        super(licensePlate, brand, model, entryTime);
    }

    @Override
    public double calculateFee(long hours) {
        double hourlyRate = 800.0;
        return hours * hourlyRate;
    }
}
