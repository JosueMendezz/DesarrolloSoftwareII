package model.entities;

/**
 * Entity representing a Clerk/Operator user. Limited access role compared to
 * Admin.
 */
public class Clerk extends User {

    public Clerk(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "OPERATOR";
    }
}
