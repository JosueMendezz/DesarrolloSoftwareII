package model.entities;

public class ParkingSpace {

    private int spaceNumber;
    private String vehicleTypesSupported;
    private boolean isPreferential;
    private boolean isOccupied;

    public ParkingSpace() {
    }

    public ParkingSpace(int spaceNumber, String vehicleTypesSupported, boolean isPreferential, boolean isOccupied) {
        this.spaceNumber = spaceNumber;
        this.vehicleTypesSupported = vehicleTypesSupported;
        this.isPreferential = isPreferential;
        this.isOccupied = isOccupied;
    }

    public int getSpaceNumber() {
        return spaceNumber;
    }

    public String getVehicleTypesSupported() {
        return vehicleTypesSupported;
    }

    public boolean isIsPreferential() {
        return isPreferential;
    }

    public boolean isIsOccupied() {
        return isOccupied;
    }

    public void setSpaceNumber(int n) {
        this.spaceNumber = n;
    }

    public void setVehicleTypesSupported(String type) {
        this.vehicleTypesSupported = type;
    }

    public void setIsPreferential(boolean p) {
        this.isPreferential = p;
    }

    public void setIsOccupied(boolean o) {
        this.isOccupied = o;
    }
}
