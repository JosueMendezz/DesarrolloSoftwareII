package view;

import controller.CustomerController;
import controller.ParkingController;
import model.data.FileManager;
import model.entities.User;
import javax.swing.*;
import java.awt.*;
import model.services.LocalReportService;

public class Dashboard extends BaseFrame {

    private final User currentUser;
    private final FileManager fileManager;
    private JTabbedPane tabbedPane;
    private BackgroundPanel mainBackground;

    public Dashboard(User currentUser, FileManager fileManager) {
        super("Heap Haven - Menú Principal", 1100, 700);
        this.currentUser = currentUser;
        this.fileManager = fileManager;

        getContentPane().setLayout(new BorderLayout());

        // 1. PANEL SUPERIOR (Barra Título + Menú)
        JPanel headerContainer = new JPanel(new BorderLayout());
        headerContainer.setBackground(COLOR_BARRA_TITULO);

        // Barra de título
        this.setupCustomTitleBar("HEAP HAVEN PARKING SYSTEM - " + currentUser.getRole());

        Component titleBar = ((BorderLayout) getContentPane().getLayout()).getLayoutComponent(BorderLayout.NORTH);
        if (titleBar != null) {
            headerContainer.add(titleBar, BorderLayout.NORTH);
        }

        // Menú de navegación justo debajo
        setupMenuBarAsComponent(headerContainer);
        getContentPane().add(headerContainer, BorderLayout.NORTH);

        // 2. CONTENIDO CENTRAL
        setupBackground();
        setupTabs();
        setupFloatingWelcome();

        setVisible(true);
    }

    private void setupBackground() {
        mainBackground = new BackgroundPanel("HH_RTX.png");
        mainBackground.setLayout(null);
        getContentPane().add(mainBackground, BorderLayout.CENTER);
    }

    private void setupTabs() {
        tabbedPane = new JTabbedPane();
        controller.VehicleController vehicleController = new controller.VehicleController(fileManager);

        // --- ESTILO DE PESTAÑAS ---
        tabbedPane.setOpaque(false);
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(33, 150, 243, 40));
                    g2.fillRect(x, y, w, h);
                    g2.setColor(COLOR_CELESTE);
                    g2.fillRect(x, y + h - 3, w, 3);
                }
            }
        });

        // Paneles de las pestañas
        JPanel welcomePanel = new JPanel();
        welcomePanel.setOpaque(false);

        ParkingMonitorView monitorTab = new ParkingMonitorView(vehicleController, currentUser);
        monitorTab.setOpaque(false);

        tabbedPane.addTab("INICIO", welcomePanel);
        tabbedPane.addTab("MONITOR DE PARQUEOS", monitorTab);

        tabbedPane.setBounds(20, 20, 1060, 500);

        customizeTabTitles();
        mainBackground.add(tabbedPane);
    }

    private void setupMenuBarAsComponent(JPanel container) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(COLOR_BARRA_TITULO);
        menuBar.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        menuBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(50, 50, 50)));

        // Estilo  para los menús
        JMenu systemMenu = createMenu("SISTEMA");
        JMenu operationsMenu = createMenu("OPERACIONES");

        // --- SISTEMA ---
        JMenuItem logoutItem = createMenuItem("Cerrar Sesión");
        logoutItem.addActionListener(e -> handleLogout());
        systemMenu.add(logoutItem);

        // --- OPERACIONES ---
        operationsMenu.add(createMenuItem("Gestión de Clientes")).addActionListener(e -> openCustomerManagement());
        operationsMenu.add(createMenuItem("Ingreso de Vehículos")).addActionListener(e -> openVehicleCheckIn());

        // --- REPORTE LOCAL ---
        JMenuItem reportItem = createMenuItem("Generar Reporte de Sede");
        reportItem.addActionListener(e -> {
            ReportSelectionDialog dialog = new ReportSelectionDialog(this, currentUser, fileManager);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                try {
                    String sedeElegida = dialog.getSelectedSede();
                    LocalReportService reportService = new LocalReportService(fileManager);

                    reportService.generateLocalPDF(sedeElegida, currentUser);

                    JOptionPane.showMessageDialog(this,
                            "Reporte generado con éxito para: " + sedeElegida,
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        operationsMenu.add(reportItem);

        if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
            operationsMenu.add(createMenuItem("Gestión de Sedes")).addActionListener(e -> openParkingManagement());

            JMenu adminMenu = createMenu("ADMINISTRACIÓN");
            adminMenu.add(createMenuItem("Gestión de Usuarios")).addActionListener(e -> openUserManagement());
            adminMenu.add(createMenuItem("Gestión de Tarifas")).addActionListener(e -> {
                new RateManagementView(this, new controller.VehicleController(fileManager), this::refreshMonitorData).setVisible(true);
            });
            menuBar.add(systemMenu);
            menuBar.add(operationsMenu);
            menuBar.add(adminMenu);
        } else {
            menuBar.add(systemMenu);
            menuBar.add(operationsMenu);
        }

        container.add(menuBar, BorderLayout.SOUTH);
    }

    private JMenu createMenu(String title) {
        JMenu menu = new JMenu(title);
        menu.setForeground(COLOR_CELESTE);
        menu.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return menu;
    }

    private JMenuItem createMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setBackground(COLOR_BARRA_TITULO);
        item.setForeground(Color.WHITE);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        item.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return item;
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

        CustomerFrame frame = new CustomerFrame(currentUser, custController);
        frame.setVisible(true);
        this.dispose();
    }

    private void openParkingManagement() {
        if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
            try {
                ParkingController parkController = new ParkingController(fileManager);
                new ParkingManagementFrame(currentUser, parkController).setVisible(true);
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "No se pudo abrir la gestión de parqueos: " + ex.getMessage());
            }
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
            view.VehicleCheckInFrame checkIn = new view.VehicleCheckInFrame(currentUser, vehicleController);

            checkIn.setVisible(true);
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al abrir Ingreso: " + ex.getMessage());
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

    private void customizeTabTitles() {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            JLabel lbl = new JLabel(tabbedPane.getTitleAt(i));
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lbl.setForeground(Color.WHITE);
            lbl.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
            tabbedPane.setTabComponentAt(i, lbl);
        }
    }

    private void setupFloatingWelcome() {
        JLabel lblWelcome = new JLabel();
        String role = currentUser.getRole().toUpperCase();
        String user = currentUser.getUsername().toUpperCase();
        String hexCeleste = String.format("#%02x%02x%02x", COLOR_CELESTE.getRed(), COLOR_CELESTE.getGreen(), COLOR_CELESTE.getBlue());
        lblWelcome.setText("<html><div style='text-align: center; font-family: Segoe UI;'>"
                + "<span style='font-size: 9pt; color: white; letter-spacing: 5px;'>SISTEMA CENTRAL DE OPERACIONES</span><br>"
                + "<span style='font-size: 16pt; color: white;'>USUARIO ACTIVO: </span>"
                + "<b style='font-size: 16pt; color: " + hexCeleste + ";'>" + role + " - " + user + "</b>"
                + "</div></html>");

        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        lblWelcome.setBounds(0, 540, 1100, 80);
        mainBackground.add(lblWelcome);
    }

    public void refreshMonitorData() {
        for (Component comp : tabbedPane.getComponents()) {
            if (comp instanceof ParkingMonitorView) {
                ((ParkingMonitorView) comp).refreshTableData();
                break;
            }
        }
    }
}
