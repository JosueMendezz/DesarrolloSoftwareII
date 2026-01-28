package main;

import view.LoginFrame;
import controller.LoginController;

public class Main {

    public static void main(String[] args) {

        // Creamos el objeto de la ventana
        LoginFrame vista = new LoginFrame();

        // Creamos el objeto del controlador y le pasamos la ventana
        // Esto conecta los botones con la lógica
        new LoginController(vista);

        // Aseguramos que se vea 
        vista.setVisible(true);
    }
}
