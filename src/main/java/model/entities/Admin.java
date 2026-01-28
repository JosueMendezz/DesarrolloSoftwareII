package model.entities;

public class Admin extends User {

    public Admin(String username, String password) {
        super(username, password); // Reutilizamos la lógica del padre
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    public void createRate() {

    }
}
