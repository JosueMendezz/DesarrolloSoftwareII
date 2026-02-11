package model.entities;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity representing a Heavy Vehicle (Trucks, Buses, etc.). Implements
 * specific fee calculation and weight properties.
 */
public class HeavyVehicle extends Vehicle {

    private static final double HOURLY_RATE = 3000.0;

    public HeavyVehicle(String plate, String type, String brand, String model, String color, String ownerId, List<String> additionalResponsible, LocalDateTime entryTime) {
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
