package model.entities;

import java.time.LocalDateTime;

public class Ticket {

    private static int counter = 1000;

    private String ticketId;
    private String parkingLotName;
    private int spaceNumber;
    private LocalDateTime entryTime;
    private Vehicle vehicle;
    private Customer customer;

    public Ticket(String parkingLotName, int spaceNumber, Vehicle vehicle, Customer customer, double hourlyRateAtEntry) {
        this.ticketId = "Ticket ID: " + (counter++);
        this.parkingLotName = parkingLotName;
        this.spaceNumber = spaceNumber;
        this.entryTime = LocalDateTime.now();
        this.vehicle = vehicle;
        this.customer = customer;
    }

    public Ticket() {
    }

    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        Ticket.counter = counter;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getParkingLotName() {
        return parkingLotName;
    }

    public void setParkingLotName(String parkingLotName) {
        this.parkingLotName = parkingLotName;
    }

    public int getSpaceNumber() {
        return spaceNumber;
    }

    public void setSpaceNumber(int spaceNumber) {
        this.spaceNumber = spaceNumber;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

}
