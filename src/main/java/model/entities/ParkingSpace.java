package model.entities;

public class ParkingSpace {
    private int spaceNumber;
    private String vehicleTypesSupported;
    private boolean isPreferential;
    private boolean isOccupied;

    public ParkingSpace() {
    }

    // --- MÉTODOS CLAVE PARA EL CONTROLADOR ---

    public void setSpaceNumber(int n) { this.spaceNumber = n; }
    public void setVehicleTypesSupported(String type) { this.vehicleTypesSupported = type; }
    public void setIsPreferential(boolean p) { this.isPreferential = p; }
    public void setIsOccupied(boolean o) { this.isOccupied = o; }
}