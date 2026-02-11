package model.entities;

/**
 * Entity representing an Administrator user. Inherits basic credentials from
 * User and defines the ADMIN role.
 */
public class Admin extends User {

    public Admin(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    /**
     * Future specialized administrative logic such as rate management should be
     * coordinated through a Controller, not directly in this Entity.
     */
    public void createRate() {
        // Reserved for future administrative features
    }
}
