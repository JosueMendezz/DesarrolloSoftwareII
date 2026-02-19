package model.entities;

import java.time.LocalDateTime;
import java.util.List;


public class Car extends Vehicle {

    private static final double HOURLY_RATE = 2000.0;

    public Car(String plate, String type, String brand, String model, String color, String ownerId, List<String> additionalResponsible, LocalDateTime entryTime) {
        super(plate, type, brand, model, color, ownerId, additionalResponsible, entryTime);
    }

    @Override
    public double getHourlyRate() {
        return HOURLY_RATE;
    }

    @Override
    public double calculateFee(long hours) {
        return hours * HOURLY_RATE;
    }

    public double calculateFee(LocalDateTime exitTime) {
        return getHoursParked(exitTime) * HOURLY_RATE;
    }
}
