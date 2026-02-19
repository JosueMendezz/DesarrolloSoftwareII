package view;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends JFrame {

    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JButton btnLogin;
    private final JButton btnExit;

    public LoginFrame() {
        setTitle("J-Node Parking System - Acceso");
        setSize(400, 380);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Usuario:"), gbc);

        gbc.gridx = 1;
        txtUsername = new JTextField(15);
        mainPanel.add(txtUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Contraseña:"), gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        mainPanel.add(txtPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        btnLogin = new JButton("Ingresar");
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setBackground(new Color(40, 167, 69));
        btnLogin.setForeground(Color.WHITE);
        mainPanel.add(btnLogin, gbc);

        gbc.gridy = 3;
        btnExit = new JButton("Cerrar Aplicación");
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExit.setFont(new Font("Arial", Font.PLAIN, 12));
        btnExit.setBackground(new Color(220, 53, 69));
        btnExit.setForeground(Color.WHITE);

        btnExit.addActionListener(e -> handleExit());

        mainPanel.add(btnExit, gbc);

        this.getRootPane().setDefaultButton(btnLogin);

        add(mainPanel);
    }

    private void handleExit() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Desea cerrar el sistema de parqueos?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    public String getUsername() {
        return txtUsername.getText().trim();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error de sistema", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Acceso concedido", JOptionPane.INFORMATION_MESSAGE);
    }

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
