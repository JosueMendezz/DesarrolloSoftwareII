package view;

import controller.CustomerController;
import controller.ParkingController;
import controller.VehicleController; // Asegúrate de importar esto
import model.data.FileManager;
import model.entities.User;
import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    private final User currentUser;
    private final FileManager fileManager;
    private JTabbedPane tabbedPane;

    public Dashboard(User currentUser, FileManager fileManager) {
        this.currentUser = currentUser;
        this.fileManager = fileManager;

        setupConfiguration();
        setupBackground();
        setupTabs();
        setupMenuBar();

        setVisible(true);
    }

    private void setupConfiguration() {
        setTitle("J-Node Parking System - Main Menu");
        setSize(1100, 700); // Un poco más grande para la tabla
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void setupBackground() {
        BackgroundPanel background = new BackgroundPanel("background.jpg");
        background.setLayout(new BorderLayout());
        setContentPane(background);
    }

    private void setupTabs() {
        tabbedPane = new JTabbedPane();

        // 1. Transparencia total del contenedor de pestañas
        tabbedPane.setBackground(new Color(45, 45, 45, 200));
        tabbedPane.setOpaque(false);

        // --- PESTAÑA 1: BIENVENIDA ---
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setOpaque(false); // <--- Clave para ver el BackgroundPanel

        // Contenedor del mensaje (el recuadro "ahumado")
        JPanel textContainer = new JPanel();
        textContainer.setBackground(new Color(0, 0, 0, 180)); // Negro semi-transparente
        textContainer.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        // IMPORTANTE: Para que el color con transparencia funcione, setOpaque debe ser true
        textContainer.setOpaque(true);

        String role = currentUser.getRole().toLowerCase();
        JLabel lblWelcome = new JLabel("Bienvenido " + role + ": " + currentUser.getUsername());
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblWelcome.setForeground(Color.WHITE);

        textContainer.add(lblWelcome);
        welcomePanel.add(textContainer);

        // --- PESTAÑA 2: MONITOR DE ESTADO ---
        VehicleController vehicleController = new VehicleController(fileManager);
        ParkingMonitorView monitorTab = new ParkingMonitorView(vehicleController);
        monitorTab.setOpaque(true); // Se mantiene opaco para legibilidad de la tabla

        // Añadimos las pestañas
        tabbedPane.addTab("Inicio", welcomePanel);
        tabbedPane.addTab("Parqueos", monitorTab);
        customizeTabTitles();
        getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // 2. AJUSTE DE UI: Algunos "Look and Feel" pintan un panel opaco debajo de las pestañas
        // Recorremos los componentes internos del tabbedPane para asegurar transparencia en la pestaña 0
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            }
        });

        // Añadimos el tabbedPane al Dashboard
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
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

        JMenuItem customerItem = new JMenuItem("Gestión de Clientes");
        customerItem.addActionListener(e -> openCustomerManagement());
        operationsMenu.add(customerItem);

        JMenuItem vehicleItem = new JMenuItem("Ingreso de Vehículos");
        vehicleItem.addActionListener(e -> openVehicleCheckIn());
        operationsMenu.add(vehicleItem);

        if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
            JMenuItem parkingItem = new JMenuItem("Gestión de Parqueos");
            parkingItem.addActionListener(e -> openParkingManagement());
            operationsMenu.add(parkingItem);
        }

        menuBar.add(systemMenu);
        menuBar.add(operationsMenu);

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
            view.LoginFrame loginView = new view.LoginFrame();
            new controller.LoginController(loginView, this.fileManager);
            loginView.setVisible(true);
        }
    }

    private void openCustomerManagement() {
        CustomerController custController = new CustomerController(fileManager);
        new CustomerFrame(currentUser, custController).setVisible(true);
        this.dispose();
    }

    private void openParkingManagement() {
        if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
            ParkingController parkController = new ParkingController(fileManager);
            new ParkingManagementFrame(currentUser, parkController).setVisible(true);
            this.dispose();
        }
    }

    private void openUserManagement() {
        if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
            try {
                controller.UserController userController = new controller.UserController(fileManager);
                new view.UserManagementFrame(currentUser, userController).setVisible(true);
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void openVehicleCheckIn() {
        try {
            controller.VehicleController vehicleController = new controller.VehicleController(fileManager);
            new view.VehicleCheckInFrame(currentUser, vehicleController).setVisible(true);
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    // Panel interno para el fondo
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

    private void customizeTabTitles() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            // Creamos un JLabel para el título
            JLabel lbl = new JLabel(tabbedPane.getTitleAt(i));
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lbl.setForeground(Color.WHITE);

            // Le damos un margen interno para que no se vea apretado
            lbl.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

            // Lo aplicamos a la pestaña
            tabbedPane.setTabComponentAt(i, lbl);
        }
    }
}
