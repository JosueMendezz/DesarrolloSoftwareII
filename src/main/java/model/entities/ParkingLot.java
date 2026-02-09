package model.entities;

import java.util.Arrays;

/**
 * Entity representing a Parking Lot or a specific parking level.
 * Stores the layout and total capacity of parking spaces.
 */
public class ParkingLot {

    private int id;
    private String name;
    private int numberOfSpaces;
    private int preferentialSpaces;
    private ParkingSpace[] spaces;

    // Constructor vacío - ¡AHORA SEGURO!
    public ParkingLot() {
        // Inicializamos con valores por defecto para evitar NullPointerException
        this.spaces = new ParkingSpace[0];
    }

    public ParkingLot(int id, String name, int numberOfSpaces, int preferentialSpaces) {
        this.id = id;
        this.name = name;
        this.numberOfSpaces = numberOfSpaces;
        this.preferentialSpaces = preferentialSpaces;
        // Inicializamos el arreglo con el tamaño exacto solicitado
        this.spaces = new ParkingSpace[numberOfSpaces];
    }

    // --- GETTERS Y SETTERS ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getNumberOfSpaces() { return numberOfSpaces; }

    /**
     * Al cambiar el número de espacios, debemos redimensionar el arreglo
     * para que el controlador tenga donde guardar los objetos ParkingSpace.
     */
    public void setNumberOfSpaces(int numberOfSpaces) {
        this.numberOfSpaces = numberOfSpaces;
        this.spaces = new ParkingSpace[numberOfSpaces];
    }

    public ParkingSpace[] getSpaces() {
        return spaces;
    }

    public void setSpaces(ParkingSpace[] spaces) {
        this.spaces = spaces;
    }

    public int getPreferentialSpaces() {
        return preferentialSpaces;
    }

    public void setPreferentialSpaces(int preferentialSpaces) {
        this.preferentialSpaces = preferentialSpaces;
    }

    @Override
    public String toString() {
        return "ParkingLot [id=" + id + ", name=" + name + ", numberOfSpaces=" + numberOfSpaces 
                + ", preferentialSpaces=" + preferentialSpaces + ", spaces=" + Arrays.toString(spaces) + "]";
    }
}