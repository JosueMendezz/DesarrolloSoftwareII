package model.entities;

public class ParkingSpace {
    private int spaceNumber;
    private String vehicleTypeSupported; 
    private boolean isPreferential;
    private boolean isOccupied;
    private Vehicle currentVehicle; 

    public ParkingSpace(int spaceNumber, String vehicleTypeSupported, boolean isPreferential) {
        this.spaceNumber = spaceNumber;
        this.vehicleTypeSupported = vehicleTypeSupported;
        this.isPreferential = isPreferential;
        this.isOccupied = false;
    }
    
    public ParkingSpace() {
    }

    public int getSpaceNumber() {
        return spaceNumber;
    }

    public void setSpaceNumber(int spaceNumber) {
        this.spaceNumber = spaceNumber;
    }

    public String getVehicleTypeSupported() {
        return vehicleTypeSupported;
    }

    public void setVehicleTypeSupported(String vehicleTypeSupported) {
        this.vehicleTypeSupported = vehicleTypeSupported;
    }

    public boolean isIsPreferential() {
        return isPreferential;
    }

    public void setIsPreferential(boolean isPreferential) {
        this.isPreferential = isPreferential;
    }

    public boolean isIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(boolean isOccupied) {
        this.isOccupied = isOccupied;
    }

    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }

    public void setCurrentVehicle(Vehicle currentVehicle) {
        this.currentVehicle = currentVehicle;
    }

    @Override
    public String toString() {
        return "ParkingSpace{" + "spaceNumber=" + spaceNumber + ", vehicleTypeSupported=" + vehicleTypeSupported + ", isPreferential=" + isPreferential + ", isOccupied=" + isOccupied + ", currentVehicle=" + currentVehicle + '}';
    }
    
}