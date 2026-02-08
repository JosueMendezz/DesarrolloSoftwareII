
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

    // creamos la ventana
    public LoginFrame() {
        
        // Configuración básica de la ventana
        setTitle("J-Node Parking System - Acceso");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla

        setLayout(new GridLayout(3, 2, 10, 10));

        // Inicializamos los componentes
        lblUser = new JLabel("Usuario:");
        lblPass = new JLabel("Contraseña:");
        txtUser = new JTextField();
        txtPass = new JPasswordField();
        btnLogin = new JButton("Ingresar");

        // Alineamos el botón y etiquetas
        lblUser.setHorizontalAlignment(SwingConstants.CENTER);
        lblPass.setHorizontalAlignment(SwingConstants.CENTER);

        // Agregamos los componentes a la ventana
        add(lblUser);
        add(txtUser);
        add(lblPass);
        add(txtPass);
        add(new JLabel(""));
        add(btnLogin);

        // Hacemos visible la ventana
        setVisible(true);
    }

    // --- MÉTODOS PARA QUE EL CONTROLADOR HABLE CON LA VISTA ---
    public String getUsername() {
        return txtUser.getText();
    }

    public String getPassword() {
        // JPasswordField requiere un manejo especial para convertir a String
        return new String(txtPass.getPassword());
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Bienvenido", JOptionPane.INFORMATION_MESSAGE);
    }

    // permite al controlador "escuchar" cuando alguien da click
    public void addLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
    }
}
