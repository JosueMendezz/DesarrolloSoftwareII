package model.entities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public abstract class Vehicle {

    private String plate;
    private String type;
    private String brand;
    private String model;
    private String color;
    private String ownerId;
    private List<String> additionalResponsible;
    protected LocalDateTime entryTime;

    public Vehicle(String plate, String type, String brand, String model, String color, String ownerId, List<String> additionalResponsible, LocalDateTime entryTime) {
        this.plate = plate;
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.color = color;
        this.ownerId = ownerId;
        this.additionalResponsible = additionalResponsible;
        this.entryTime = entryTime;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<String> getAdditionalResponsible() {
        return additionalResponsible;
    }

    public void setAdditionalResponsible(List<String> additionalResponsible) {
        this.additionalResponsible = additionalResponsible;
    }

    public abstract double getHourlyRate();

    public abstract double calculateFee(long hours);

    public long getHoursParked(LocalDateTime exitTime) {
        if (entryTime == null) {
            return 0;
        }
        Duration duration = Duration.between(entryTime, exitTime);
        long hours = duration.toHours();
        return (duration.toMinutes() % 60 > 0) ? hours + 1 : hours;
    }
}
