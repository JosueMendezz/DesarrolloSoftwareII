package controller;

import java.io.IOException;
import java.util.List;
import model.data.FileManager;
import model.entities.User;
import view.LoginFrame;
import view.Dashboard;
import javax.swing.JOptionPane;

public class LoginController {

    private final FileManager dataManager;
    private final LoginFrame view;

    public LoginController(LoginFrame view, FileManager dataManager) {
        this.view = view;
        this.dataManager = dataManager;
        initListeners();
    }

    private void initListeners() {
        view.getBtnLogin().addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        try {
            String userStr = view.getTxtUser().getText();
            String passStr = new String(view.getTxtPassword().getPassword());

            User user = authenticate(userStr, passStr);

            JOptionPane.showMessageDialog(view, "Bienvenido " + user.getRole() + ": " + user.getUsername());

            new Dashboard(user, dataManager);
            view.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, ex.getMessage(), "Error de Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    public User authenticate(String username, String password) throws IOException, Exception {
        validateInput(username, password);

        List<User> users = dataManager.loadUsers();
        validateUserList(users);

        User authenticatedUser = findUser(username, password, users);

        if (authenticatedUser == null) {
            throw new Exception("Usuario o contraseña incorrectos. Intente de nuevo.");
        }

        return authenticatedUser;
    }

    private void validateInput(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            throw new Exception("Por favor, ingrese sus credenciales (usuario y contraseña).");
        }
    }

    private void validateUserList(List<User> users) throws Exception {
        if (users == null || users.isEmpty()) {
            throw new Exception("No hay usuarios registrados en el sistema.");
        }
    }

    private User findUser(String username, String password, List<User> users) {
        for (User user : users) {
            if (user.getUsername().equals(username.trim())
                    && user.getPassword().equals(password.trim())) {
                return user;
            }
        }
        return null;
    }
}
