package view;

import model.entities.User;
import controller.LoginController;
import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    private JLabel lblWelcome;
    private User currentUser;

    public Dashboard(User currentUser) {
        this.currentUser = currentUser;

        // Basic frame config
        setTitle("Sistema de Parqueo J-Node - Menú Principal");
        setSize(1024, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set background panel
        BackgroundPanel background = new BackgroundPanel("background.jpg");
        background.setLayout(new GridBagLayout());
        setContentPane(background);

        // Init components
        setupWelcomeMessage();
        setupMenuBar();

        setVisible(true);
    }

    private void setupWelcomeMessage() {
        // UI text in Spanish for client
        lblWelcome = new JLabel("Bienvenido " + currentUser.getRole().toLowerCase() + ": " + currentUser.getUsername());
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcome.setForeground(Color.WHITE);

        JPanel textContainer = new JPanel();
        textContainer.setBackground(new Color(0, 0, 0, 120));
        textContainer.add(lblWelcome);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(45, 0, 0, 0);
        this.getContentPane().add(textContainer, gbc);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(20, 20, 20));

        // System Menu
        JMenu systemMenu = new JMenu("Sistema");
        systemMenu.setForeground(Color.WHITE);

        JMenuItem logoutItem = new JMenuItem("Cerrar Sesión");
        logoutItem.addActionListener(e -> {
            // Close current dashboard
            this.dispose();

            // Restart Login flow with its controller
            LoginFrame loginView = new LoginFrame();
            new LoginController(loginView);
            loginView.setVisible(true);
        });
        systemMenu.add(logoutItem);

        // Operations Menu
        JMenu operationsMenu = new JMenu("Operaciones");
        operationsMenu.setForeground(Color.WHITE);

        JMenuItem customerItem = new JMenuItem("Gestión de Clientes");
        customerItem.addActionListener(e -> {
            // Open customer management
            CustomerFrame customerView = new CustomerFrame();
            customerView.setVisible(true);
        });
        operationsMenu.add(customerItem);

        menuBar.add(systemMenu);
        menuBar.add(operationsMenu);

        // Admin restricted menu
        if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
            JMenu adminMenu = new JMenu("Administración");
            adminMenu.setForeground(Color.WHITE);

            JMenuItem userMgmtItem = new JMenuItem("Gestión de Usuarios");
            userMgmtItem.addActionListener(e -> {
                // Open user management
                UserManagementFrame userFrame = new UserManagementFrame();
                userFrame.setVisible(true);
            });

            adminMenu.add(userMgmtItem);
            menuBar.add(adminMenu);
        }

        setJMenuBar(menuBar);
    }

    // Inner class for background image rendering
    class BackgroundPanel extends JPanel {

        private Image img;

        public BackgroundPanel(String path) {
            this.img = new ImageIcon(path).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null) {
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
