package view;

import controller.CustomerController;
import controller.ParkingController;
import model.data.FileManager;
import model.entities.User;
import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    private final User currentUser;
    private final FileManager fileManager;
    private JLabel lblWelcome;

    public Dashboard(User currentUser, FileManager fileManager) {
        this.currentUser = currentUser;
        this.fileManager = fileManager;

        setupConfiguration();
        setupBackground();
        setupWelcomeMessage();
        setupMenuBar();

        setVisible(true);
    }

    private void setupConfiguration() {
        setTitle("J-Node Parking System - Main Menu");
        setSize(1024, 600);
        // Cambiamos a EXIT_ON_CLOSE para que al cerrar el menú principal se detenga todo el programa
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void setupBackground() {
        BackgroundPanel background = new BackgroundPanel("background.jpg");
        background.setLayout(new GridBagLayout());
        setContentPane(background);
    }

    private void setupWelcomeMessage() {
        String role = currentUser.getRole().toLowerCase();
        lblWelcome = new JLabel("Bienvenido " + role + ": " + currentUser.getUsername());
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcome.setForeground(Color.WHITE);

        JPanel textContainer = new JPanel();
        textContainer.setBackground(new Color(0, 0, 0, 120));
        textContainer.add(lblWelcome);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(45, 0, 0, 0);
        getContentPane().add(textContainer, gbc);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(20, 20, 20));

        // --- System Menu ---
        JMenu systemMenu = createMenu("Sistema");
        JMenuItem logoutItem = new JMenuItem("Cerrar Sesión");
        logoutItem.addActionListener(e -> handleLogout());
        systemMenu.add(logoutItem);

        // --- Operations Menu ---
        JMenu operationsMenu = createMenu("Operaciones");

        // El Customer Management sigue siendo para todos
        JMenuItem customerItem = new JMenuItem("Gestión de Clientes");
        customerItem.addActionListener(e -> openCustomerManagement());
        operationsMenu.add(customerItem);

        // SEGURIDAD: Solo el ADMIN ve la opción de Parking Management
        if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
            JMenuItem parkingItem = new JMenuItem("Gestión de Parqueos");
            parkingItem.addActionListener(e -> openParkingManagement());
            operationsMenu.add(parkingItem);
        }

        menuBar.add(systemMenu);
        menuBar.add(operationsMenu);

        // --- Admin Restricted Menu (User Management) ---
        if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
            JMenu adminMenu = createMenu("Administración");
            JMenuItem userMgmtItem = new JMenuItem("Gestión de Usuarios");
            userMgmtItem.addActionListener(e -> openUserManagement());
            adminMenu.add(userMgmtItem);
            menuBar.add(adminMenu);
        }

        setJMenuBar(menuBar);
    }

    private JMenu createMenu(String title) {
        JMenu menu = new JMenu(title);
        menu.setForeground(Color.WHITE);
        return menu;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que deseas cerrar sesión?",
                "Cerrar Sesión", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            model.data.FileManager fm = new model.data.FileManager();
            view.LoginFrame loginView = new view.LoginFrame();
            new controller.LoginController(loginView, fm);
            loginView.setVisible(true);
        }
    }

    // --- MÉTODOS CORREGIDOS CON DISPOSE PARA EVITAR VENTANAS DUPLICADAS ---
    private void openCustomerManagement() {
        CustomerController custController = new CustomerController(fileManager);
        new CustomerFrame(currentUser, custController).setVisible(true);
        this.dispose(); // <--- CERRAMOS EL DASHBOARD
    }

    private void openParkingManagement() {
    // Doble validación de seguridad
    if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
        ParkingController parkController = new ParkingController(fileManager);
        new ParkingManagementFrame(currentUser, parkController).setVisible(true);
        this.dispose(); 
    } else {
        JOptionPane.showMessageDialog(this, 
            "Access Denied: Only Administrators can manage parking branches.", 
            "Security Warning", 
            JOptionPane.WARNING_MESSAGE);
    }
}

    private void openUserManagement() {
    // 1. Verificación de seguridad redundante
    if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
        try {
            // 2. Instanciar el controlador de usuarios (asegúrate de que el nombre sea correcto)
            // Se asume que sigue el patrón de recibir el fileManager
            controller.UserController userController = new controller.UserController(fileManager);
            
            // 3. Abrir la ventana de gestión de usuarios (UserManagementFrame)
            // Pasamos el usuario actual y el controlador
            new view.UserManagementFrame(currentUser, userController).setVisible(true);
            
            // 4. Cerramos el Dashboard para evitar duplicados
            this.dispose(); 
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading User Management: " + ex.getMessage(), 
                                        "System Error", JOptionPane.ERROR_MESSAGE);
        }
    } else {
        JOptionPane.showMessageDialog(this, "User Management is only for System Admins.", 
                                    "Access Denied", JOptionPane.WARNING_MESSAGE);
    }
}

    class BackgroundPanel extends JPanel {

        private Image img;

        public BackgroundPanel(String path) {
            try {
                this.img = new ImageIcon(path).getImage();
            } catch (Exception e) {
                this.img = null;
            }
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
