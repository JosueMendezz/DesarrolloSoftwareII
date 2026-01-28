package model.entities;

import java.util.ArrayList;

public class ParkingLot {

    private int id;
    private String name;
    private int numberOfSpaces;
    private ParkingSpace[] spaces;

    public ParkingLot() {
    }

    public ParkingLot(int id, String name, int numberOfSpaces, ArrayList<Vehicle> vehicles, ParkingSpace[] spaces) {
        this.id = id;
        this.name = name;
        this.numberOfSpaces = numberOfSpaces;
        this.spaces = new ParkingSpace[numberOfSpaces];
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfSpaces() {
        return numberOfSpaces;
    }

    public void setNumberOfSpaces(int numberOfSpaces) {
        this.numberOfSpaces = numberOfSpaces;
    }

    public ParkingSpace[] getSpaces() {
        return spaces;
    }

    public void setSpaces(ParkingSpace[] spaces) {
        this.spaces = spaces;
    }

    @Override
    public String toString() {
        return "ParkingLot{" + "id=" + id + ", name=" + name + ", numberOfSpaces=" + numberOfSpaces + ", spaces=" + spaces + '}';
    }

}
