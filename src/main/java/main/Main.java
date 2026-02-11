package main;

import model.data.FileManager;
import view.LoginFrame;
import controller.LoginController;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {

        FileManager fileManager = new FileManager();

        LoginFrame loginView = new LoginFrame();

        new LoginController(loginView, fileManager);

        loginView.setVisible(true);
    }
}
