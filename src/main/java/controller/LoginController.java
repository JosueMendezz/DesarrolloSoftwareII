package controller;

import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import model.data.FileManager;
import model.entities.User;
import view.LoginFrame;

public class LoginController {

    private LoginFrame view;
    private FileManager dataManager;

    public LoginController(LoginFrame view) {
        
        this.view = view;
        this.dataManager = new FileManager();

        // conectamos el btn
        
        this.view.addLoginListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                try {
                    authenticate();
                } catch (IOException ex) {
                    view.showError("Error crítico: " + ex.getMessage());
                }
                
            }
        });
    }

    public void authenticate() throws IOException {
        String user = view.getUsername();
        String pass = view.getPassword();

        // Validar campos vacíos
        if (user.isEmpty() || pass.isEmpty()) {
            
            view.showError("Campos vacíos no permitidos");
            
            return;
            
        }

        // Cargar usuarios
        List<User> users = dataManager.loadUsers();
        
        boolean found = false;

        // Buscar coincidencia
        for (User searchUser : users) {

            if (searchUser.getUsername().equals(user) && searchUser.getPassword().equals(pass)) {
                view.showSuccess("Bienvenido " + searchUser.getRole());
                found = true;

                view.dispose(); 
                
                new view.Dashboard(searchUser); 

                break;
            }
        }

        if (!found) {
            
            view.showError("Usuario o contraseña incorrectos");
            
        }
    }
}
