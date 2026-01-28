package main;

import view.LoginFrame;
import controller.LoginController;

public class Main {

    public static void main(String[] args) {

        LoginFrame vista = new LoginFrame();

        new LoginController(vista);

        vista.setVisible(true);
    }
}
