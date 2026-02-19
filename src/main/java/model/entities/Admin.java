package model.entities;

public class Admin extends User {

    public Admin(String username, String password, String fullName, String assignedParking) {
        super(username, password, fullName, assignedParking);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

}
