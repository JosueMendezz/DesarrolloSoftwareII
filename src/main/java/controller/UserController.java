package controller;

import model.data.FileManager;
import model.entities.Admin;
import model.entities.Clerk;
import model.entities.User;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class UserController {

    private final FileManager fileManager;
    private List<User> cachedUsers;

    public UserController(FileManager fileManager) {
        this.fileManager = fileManager;
        this.cachedUsers = new ArrayList<>();
    }

    public void loadInitialUsers() throws IOException {
        this.cachedUsers = fileManager.loadUsers();
    }

    public void processUserRegistration(String user, String pass, String role, String name, String sede) throws Exception {
        if (user.isEmpty() || pass.isEmpty() || name.isEmpty()) {
            throw new Exception("Error: Todos los campos son obligatorios.");
        }

        if (isUsernameDuplicate(user)) {
            throw new Exception("El nombre de usuario ya existe.");
        }

        User newUser = role.equalsIgnoreCase("ADMIN")
                ? new Admin(user, pass, name, sede)
                : new Clerk(user, pass, name, sede);

        cachedUsers.add(newUser);
        fileManager.saveUsers(cachedUsers);
    }

    public void processUserUpdate(String username, String password, String role, String name, String sede) throws Exception {
        validateInput(username, password);

        int index = findUserIndex(username);
        if (index == -1) {
            throw new Exception("Usuario no encontrado, seleccione un Usuario existente para modificar");
        }

        User updatedUser = role.equalsIgnoreCase("ADMIN")
                ? new Admin(username, password, name, sede)
                : new Clerk(username, password, name, sede);

        cachedUsers.set(index, updatedUser);
        fileManager.saveUsers(cachedUsers);
    }

    public void processUserDeletion(String username) throws Exception {
        int index = findUserIndex(username);
        if (index == -1) {
            throw new Exception("El usuario no se ha encontrado en la base de datos");
        }

        cachedUsers.remove(index);
        fileManager.saveUsers(cachedUsers);
    }

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

    public FileManager getFileManager() {
        return this.fileManager;
    }

    public List<String[]> getAllUsers() {
        List<String> lines = fileManager.readAllUserLines();
        List<String[]> userList = new ArrayList<>();

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                userList.add(line.split(","));
            }
        }
        return userList;
    }

    public User validateLogin(String username, String password) {
        List<String[]> allUsers = getAllUsers();

        for (String[] row : allUsers) {
            if (row[0].equals(username) && row[1].equals(password)) {
                String role = row[2];
                String name = row[3];
                String sede = row[4];

                if (role.equalsIgnoreCase("ADMIN")) {
                    return new Admin(row[0], row[1], name, sede);
                } else {
                    return new Clerk(row[0], row[1], name, sede);
                }
            }
        }
        return null;
    }
}
