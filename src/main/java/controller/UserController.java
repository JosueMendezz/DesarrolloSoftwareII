package controller;

import model.data.FileManager;
import model.entities.Admin;
import model.entities.Clerk;
import model.entities.User;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Controller for User Management.
 * Bridge between UserManagementFrame and FileManager.
 */
public class UserController {

    private final FileManager fileManager;
    private List<User> cachedUsers; // Local state to avoid constant disk reading

    public UserController(FileManager fileManager) {
        this.fileManager = fileManager;
        this.cachedUsers = new ArrayList<>();
    }

    /**
     * Initial data load for the view's table.
     */
    public void loadInitialUsers() throws IOException {
        this.cachedUsers = fileManager.loadUsers();
    }

    /**
     * Logic for registering a new user.
     */
    public void processUserRegistration(String username, String password, String role) throws Exception {
        validateInput(username, password);
        
        if (isUsernameDuplicate(username)) {
            throw new Exception("Username already exists.");
        }

        User newUser = role.equalsIgnoreCase("ADMIN") 
                ? new Admin(username, password) 
                : new Clerk(username, password);

        cachedUsers.add(newUser);
        fileManager.saveUsers(cachedUsers);
    }

    /**
     * Logic for updating an existing user.
     */
    public void processUserUpdate(String username, String password, String role) throws Exception {
        validateInput(username, password);

        int index = findUserIndex(username);
        if (index == -1) throw new Exception("User not found for update.");

        User updatedUser = role.equalsIgnoreCase("ADMIN")
                ? new Admin(username, password)
                : new Clerk(username, password);

        cachedUsers.set(index, updatedUser);
        fileManager.saveUsers(cachedUsers);
    }

    /**
     * Logic for deleting a user.
     */
    public void processUserDeletion(String username) throws Exception {
        int index = findUserIndex(username);
        if (index == -1) throw new Exception("El usuario no se ha encontrado en la base de datos");

        cachedUsers.remove(index);
        fileManager.saveUsers(cachedUsers);
    }

    // --- Internal Helpers ---

    private void validateInput(String user, String pass) throws Exception {
        if (user.isEmpty() || pass.isEmpty()) {
            throw new Exception("Los campos no pueden quedar vacíos.");
        }
    }

    private boolean isUsernameDuplicate(String username) {
        return cachedUsers.stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }

    private int findUserIndex(String username) {
        for (int i = 0; i < cachedUsers.size(); i++) {
            if (cachedUsers.get(i).getUsername().equalsIgnoreCase(username)) {
                return i;
            }
        }
        return -1;
    }

    public List<User> getCachedUsers() {
        return cachedUsers;
    }
    
    // Dentro de UserController.java
public FileManager getFileManager() {
    return this.fileManager;
}

public List<String[]> getAllUsers() {
    List<String> lines = fileManager.readAllUserLines(); // Lee el archivo de usuarios
    List<String[]> userList = new ArrayList<>();
    
    for (String line : lines) {
        if (!line.trim().isEmpty()) {
            // Corta la línea por la coma: admin,1234,ADMIN
            userList.add(line.split(",")); 
        }
    }
    return userList;
}
}