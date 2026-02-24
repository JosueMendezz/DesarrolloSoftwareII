package controller;

import java.util.List;
import model.data.FileManager;
import model.entities.User;
import view.LoginFrame;
import view.Dashboard;

public class LoginController {

    private final FileManager dataManager;
    private final LoginFrame view;

    public LoginController(LoginFrame view, FileManager dataManager) {
        this.view = view;
        this.dataManager = dataManager;
        // No instanciamos la vista, la recibimos por parámetro
        initListeners();
    }

    private void initListeners() {
        // El controlador solo coordina, no maneja el error aquí
        view.getBtnLogin().addActionListener(e -> {
            try {
                handleLogin();
            } catch (Exception ex) {
                // La vista es la encargada de mostrar el error al usuario
                view.showErrorMessage(ex.getMessage());
            }
        });
    }

    // El método ahora lanza la excepción hacia el listener de la vista
    private void handleLogin() throws Exception {
        String userStr = view.getUsername();
        String passStr = view.getPassword();

        // Lógica de autenticación
        User user = authenticate(userStr, passStr);

        // Notificamos éxito y delegamos la navegación
        view.showSuccessMessage("Bienvenido " + user.getRole() + ": " + user.getUsername());
        
        // Delegamos la apertura del Dashboard (la vista se encarga de sí misma)
        new Dashboard(user, dataManager).setVisible(true);
        view.dispose();
    }

    public User authenticate(String username, String password) throws Exception {
        validateInput(username, password);

        List<User> users = dataManager.loadUsers();
        if (users == null || users.isEmpty()) {
            throw new Exception("No hay usuarios registrados en el sistema.");
        }

        User authenticatedUser = findUser(username, password, users);

        if (authenticatedUser == null) {
            throw new Exception("Usuario o contraseña incorrectos.");
        }

        return authenticatedUser;
    }

    private void validateInput(String username, String password) throws Exception {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            throw new Exception("Por favor, ingrese sus credenciales.");
        }
    }

    private User findUser(String username, String password, List<User> users) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username.trim()) && 
                             u.getPassword().equals(password.trim()))
                .findFirst()
                .orElse(null);
    }
}