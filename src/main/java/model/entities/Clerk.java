package model.entities;


public class Clerk extends User {

    public Clerk(String username, String password, String fullName, String assignedParking) {
        super(username, password, fullName, assignedParking);
    }

    @Override
    public String getRole() {
        return "OPERATOR";
    }
}
