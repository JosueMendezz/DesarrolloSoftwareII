
package model.entities;

public class Clerk extends User {
    
    public Clerk(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "OPERATOR";
    }
    
}
