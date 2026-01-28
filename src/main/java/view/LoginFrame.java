package view;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.*;

public class LoginFrame extends JFrame {

    private JLabel lblUser;
    private JLabel lblPass;
    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;

    public LoginFrame() {

        setTitle("J-Node Parking System - Acceso");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new GridLayout(3, 2, 10, 10));

        lblUser = new JLabel("Usuario:");
        lblPass = new JLabel("Contraseña:");
        txtUser = new JTextField();
        txtPass = new JPasswordField();
        btnLogin = new JButton("Ingresar");

        lblUser.setHorizontalAlignment(SwingConstants.CENTER);
        lblPass.setHorizontalAlignment(SwingConstants.CENTER);

        add(lblUser);
        add(txtUser);
        add(lblPass);
        add(txtPass);
        add(new JLabel(""));
        add(btnLogin);

        setVisible(true);
    }

    public String getUsername() {
        return txtUser.getText();
    }

    public String getPassword() {

        return new String(txtPass.getPassword());
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Bienvenido", JOptionPane.INFORMATION_MESSAGE);
    }

    public void addLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
    }
}
