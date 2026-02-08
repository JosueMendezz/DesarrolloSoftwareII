package controller;

import model.data.FileManager;
import model.entities.Admin;
import model.entities.Clerk;
import model.entities.User;
import view.UserManagementFrame;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserController {

    private UserManagementFrame view;
    private FileManager fileManager;

    public UserController(UserManagementFrame view) {
        this.view = view;
        this.fileManager = new FileManager();
    }

    // Process new user registration
    public void processUserRegistration() throws IOException, IllegalArgumentException {
        String username = view.getUsername().trim();
        String password = view.getPassword().trim();
        String role = view.getSelectedRole();

        if (username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("El usuario o la contraseña no pueden estar vacías.");
        }

        if (isUsernameDuplicate(username)) {
            throw new IllegalArgumentException("El usuario ya existe.");
        }

        fileManager.saveNewUser(username, password, role);
        view.getTableModel().addRow(new Object[]{username, password, role});
        view.clearFields();
    }

    // Check for duplicate usernames
    private boolean isUsernameDuplicate(String username) {
        for (int i = 0; i < view.getTableModel().getRowCount(); i++) {
            if (view.getTableModel().getValueAt(i, 0).toString().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    // Remove user from system
    public void processUserDeletion() throws IOException, IllegalStateException {
        int row = view.getTable().getSelectedRow();
        if (row < 0) {
            throw new IllegalStateException("Seleccione un usuario a eliminar.");
        }

        view.getTableModel().removeRow(row);
        syncUserData();
        view.clearFields();
    }

    // Update user data with validation
    public void processUserUpdate() throws IOException, IllegalStateException, IllegalArgumentException {
        int row = view.getTable().getSelectedRow();
        if (row < 0) {
            throw new IllegalStateException("Seleccione un usuario para actualizar.");
        }

        String updatedPassword = view.getPassword().trim();
        String updatedRole = view.getSelectedRole();

        if (updatedPassword.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }

        view.getTableModel().setValueAt(updatedPassword, row, 1);
        view.getTableModel().setValueAt(updatedRole, row, 2);

        syncUserData();
        view.clearFields();
    }

    // Sync all table users to file
    private void syncUserData() throws IOException {
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < view.getTableModel().getRowCount(); i++) {
            String u = view.getTableModel().getValueAt(i, 0).toString();
            String p = view.getTableModel().getValueAt(i, 1).toString();
            String r = view.getTableModel().getValueAt(i, 2).toString();

            if (r.equals("ADMIN")) {
                userList.add(new Admin(u, p));
            } else {
                userList.add(new Clerk(u, p));
            }
        }
        fileManager.overwriteUsers(userList);
    }

    // Load users on startup
    public void initUserLoad() throws IOException {
        List<User> users = fileManager.loadUsers();
        for (User user : users) {
            view.getTableModel().addRow(new Object[]{
                user.getUsername(),
                user.getPassword(),
                user.getRole()
            });
        }
    }
}
