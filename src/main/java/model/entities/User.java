package model.entities;

public abstract class User {

    private String username;
    private String password;
    private String fullName;
    private String assignedParking;

    public User() {
    }

    public User(String username, String password, String fullName, String assignedParking) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.assignedParking = assignedParking;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAssignedParking() {
        return assignedParking;
    }

    public void setAssignedParking(String assignedParking) {
        this.assignedParking = assignedParking;
    }

    public abstract String getRole();

}
