package view;

import controller.CustomerController;
import model.entities.User;
import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    private JLabel lblWelcome;
    private JMenuBar menuBar;
    private JMenu operationsMenu;
    private JMenu menuAdmin;
    private JMenuItem vehicleEntryItem;
    private JMenuItem vehicleExitItem;
    private JMenuItem customerManagementItem;
    private JMenuItem rateConfigurationItem;

    public Dashboard(User currentUser) {

        setTitle("J-Node System - Menú Principal");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        lblWelcome = new JLabel("Bienvenido: " + currentUser.getUsername() + " [" + currentUser.getRole() + "]");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 18));
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblWelcome, BorderLayout.CENTER);

        menuBar = new JMenuBar();

        operationsMenu = new JMenu("Operaciones");
        vehicleEntryItem = new JMenuItem("Ingresar Vehículo");
        vehicleExitItem = new JMenuItem("Retirar Vehículo");
        customerManagementItem = new JMenuItem("Gestión de Clientes");

        customerManagementItem.addActionListener(e -> {

            CustomerFrame customerView = new CustomerFrame();
            new CustomerController(customerView);
            customerView.setVisible(true);
        });

        operationsMenu.add(vehicleEntryItem);
        operationsMenu.add(vehicleExitItem);
        operationsMenu.add(customerManagementItem);
        menuBar.add(operationsMenu);

        if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {

            menuAdmin = new JMenu("Administración");
            rateConfigurationItem = new JMenuItem("Configurar Tarifas");

            menuAdmin.add(rateConfigurationItem);
            menuBar.add(menuAdmin);

            lblWelcome.setForeground(Color.BLUE);

        }
        setJMenuBar(menuBar);

        setVisible(true);
    }
}
