package view;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Pure View for the Login screen. Strictly manages UI components and exposes
 * data through getters.
 */
public class LoginFrame extends JFrame {

    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JButton btnLogin;

    public LoginFrame() {
        // Basic configuration
        setTitle("J-Node Parking System - Access");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Panel with GridBagLayout for better alignment
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // User Components
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        mainPanel.add(txtUsername, gbc);

        // Password Components
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        mainPanel.add(txtPassword, gbc);

        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        btnLogin = new JButton("Login");
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        mainPanel.add(btnLogin, gbc);

        add(mainPanel);
    }

    public String getUsername() {
        return txtUsername.getText().trim();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    // --- UI Feedback Methods ---
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "System Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Access Granted", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Delegates event handling to the Controller.
     */
    public void addLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
    }

    public JButton getBtnLogin() {
        return btnLogin; 
    }

    public JTextField getTxtUser() {
        return txtUsername;
    }

    public JPasswordField getTxtPassword() {
        return txtPassword;
    }
}
