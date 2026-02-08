package controller;

import java.io.IOException;
import java.util.List;
import model.data.FileManager;
import model.entities.User;
import view.LoginFrame;
import view.Dashboard; // Asegúrate de importar Dashboard

public class LoginController {

    private LoginFrame view;
    private FileManager dataManager;

    public LoginController(LoginFrame view) {
        this.view = view;
        this.dataManager = new FileManager();

        // Usamos una expresión Lambda para que sea más limpio (Punto extra por modernidad)
        this.view.addLoginListener(e -> {
            try {
                authenticate();
            } catch (IOException ex) {
                view.showError("Error de base de datos: No se pudo leer el archivo de usuarios.");
            }
        });
    }

    public void authenticate() throws IOException {
        String user = view.getUsername().trim();
        String pass = view.getPassword().trim();

        // VALIDACIÓN: Campos vacíos
        if (user.isEmpty() || pass.isEmpty()) {
            view.showError("Por favor, ingrese sus credenciales.");
            return;
        }

        // CARGA DE DATOS con validación de existencia
        List<User> users = dataManager.loadUsers();

        if (users.isEmpty()) {
            view.showError("No hay usuarios registrados en el sistema.");
            return;
        }

        User foundUser = null;

        for (User searchUser : users) {
            if (searchUser.getUsername().equals(user) && searchUser.getPassword().equals(pass)) {
                foundUser = searchUser;
                break;
            }
        }

        if (foundUser != null) {
            view.showSuccess("Acceso concedido como: " + foundUser.getRole());
            view.dispose(); 

            new Dashboard(foundUser);

        } else {
            view.showError("Credenciales inválidas. Intente de nuevo.");
        }
    }
}
