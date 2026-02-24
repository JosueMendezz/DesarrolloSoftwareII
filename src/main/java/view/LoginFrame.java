package view;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginFrame extends BaseFrame {

    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JButton btnLogin;
    private final JButton btnExit;

    public LoginFrame() {
        super("HEAP HAVEN - SISTEMA DE GESTIÓN", 420, 450);

        getContentPane().setLayout(new BorderLayout());
        this.setupCustomTitleBar("HEAP HAVEN - SISTEMA DE GESTIÓN");
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel lblLogo = createHeaderLabel("BIENVENIDO");
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(lblLogo, gbc);
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(createLabel("USUARIO:"), gbc);
        gbc.gridx = 1;
        txtUsername = createStyledTextField();
        mainPanel.add(txtUsername, gbc);
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(createLabel("CONTRASEÑA:"), gbc);
        gbc.gridx = 1;
        txtPassword = createStyledPasswordField();
        mainPanel.add(txtPassword, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(25, 5, 10, 5);
        btnLogin = createStyledButton("Ingresar al Sistema", true);
        mainPanel.add(btnLogin, gbc);
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 5, 10, 5);
        btnExit = createStyledButton("Cerrar Aplicación", false);
        btnExit.addActionListener(e -> handleExit());
        mainPanel.add(btnExit, gbc);
        getContentPane().add(mainPanel, BorderLayout.CENTER);
        this.getRootPane().setDefaultButton(btnLogin);
        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(COLOR_TEXTO);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return lbl;
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField(15);
        styleInput(tf);
        return tf;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField pf = new JPasswordField(15);
        styleInput(pf);
        return pf;
    }

    private void styleInput(JTextField input) {
        input.setBackground(new Color(40, 40, 40));
        input.setForeground(Color.WHITE);
        input.setCaretColor(COLOR_CELESTE);
        input.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        input.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
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
