package main;

import model.data.FileManager;
import view.LoginFrame;
import controller.LoginController;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
    // ... (Look and Feel igual)

    // 1. Inicializamos la persistencia
    FileManager fileManager = new FileManager();

    // 2. La Vista NO recibe el fileManager (Mantenemos SRP)
    LoginFrame loginView = new LoginFrame(); // Volvemos al constructor vacío

    // 3. El CONTROLADOR es el que une a ambos
    // Él recibe la vista para escuchar los botones y el manager para validar datos
    new LoginController(loginView, fileManager);    

    // 4. Lanzar
    loginView.setVisible(true);
}
}