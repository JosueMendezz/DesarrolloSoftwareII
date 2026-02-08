package view;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;

    public LoginFrame() {
        setTitle("J-Node Parking System - Acceso");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // MAIN PANEL
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // USER
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Usuario:"), gbc);
        gbc.gridx = 1;
        txtUser = new JTextField(15); // Tamaño controlado
        mainPanel.add(txtUser, gbc);

        // PASSWORD
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        txtPass = new JPasswordField(15); // Tamaño controlado
        mainPanel.add(txtPass, gbc);

        // BUTTON
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        btnLogin = new JButton("Ingresar");
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        mainPanel.add(btnLogin, gbc);

        add(mainPanel);
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
