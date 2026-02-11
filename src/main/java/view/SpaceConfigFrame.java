package view;

import controller.ParkingController;
import model.entities.User;
import javax.swing.*;
import java.awt.*;

/**
 * View for configuring individual space attributes (type and preferential
 * status). Operates on a block-allocation logic until the total capacity is
 * met.
 */
public class SpaceConfigFrame extends JFrame {

    private final JLabel lblRemaining = new JLabel("Espacios Restantes: 0");
    private final JComboBox<Integer> comboQtyToConfig = new JComboBox<>();
    private final JCheckBox chkIsPreferential = new JCheckBox("Espacio(s) Preferencial(s)");
    private final JComboBox<String> comboVehicleType = new JComboBox<>(
            new String[]{"Automóvil", "Motocicleta", "Bicicleta", "Vehículo Pesado"}
    );

    private final JButton btnExit = new JButton("Salir");
    private final JButton btnBack = new JButton("Atrás");
    private final JButton btnFinish = new JButton("Crear Parqueo");

    private final ParkingController controller;
    private final User currentUser;
    private final String oldName;

    public SpaceConfigFrame(ParkingController controller, User user, String originalName) {
        this.controller = controller;
        this.currentUser = user;
        this.oldName = originalName;

        setupConfiguration();
        setupComponents();
        setupListeners();
        updateUI();
    }

    private void setupConfiguration() {
        setTitle("J-Node - Space Allocation Config");
        setSize(500, 400);
        setLayout(new BorderLayout(15, 15));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    private void setupComponents() {
        JPanel panelMain = new JPanel(new GridLayout(5, 1, 10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Status Label
        lblRemaining.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelMain.add(lblRemaining);

        // Quantity Row
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Espacios a Asignar:"));
        row1.add(comboQtyToConfig);
        panelMain.add(row1);

        panelMain.add(chkIsPreferential);

        // Vehicle Type Row
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Tipo de Vehículo:"));
        row2.add(comboVehicleType);
        panelMain.add(row2);

        add(panelMain, BorderLayout.CENTER);

        // Navigation Panel
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButtons.add(btnExit);
        panelButtons.add(btnBack);
        panelButtons.add(btnFinish);
        add(panelButtons, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        btnFinish.addActionListener(e -> handleFinishBlock());

        btnBack.addActionListener(e -> {
            new ParkingCreateFrame(currentUser, controller, controller.getTempParking()).setVisible(true);
            this.dispose();
        });

        btnExit.addActionListener(e -> handleExit());

        chkIsPreferential.addActionListener(e -> updateUI());
    }

    private void handleFinishBlock() {
        try {
            int qty = (int) comboQtyToConfig.getSelectedItem();
            String type = (String) comboVehicleType.getSelectedItem();
            boolean isPref = chkIsPreferential.isSelected();

            controller.configureSpaceBlock(qty, isPref, type);

            if (controller.isConfigFinished()) {
                finalizeParkingSetup();
            } else {
                JOptionPane.showMessageDialog(this, "Espacio(s) configurado(s). " + controller.getRemainingSpaces() + " Espacios Restantes.");
                updateUI();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Configuration Error", JOptionPane.ERROR_MESSAGE);
            updateUI();
        }
    }

    private void finalizeParkingSetup() {
        try {
            // We delegate the saving process to the controller
            controller.saveParkingConfiguration(oldName);

            JOptionPane.showMessageDialog(this, "Parqueo Creado Exitosamente");
            // Return to management with an injected controller (Standard practice)
            new ParkingManagementFrame(currentUser, controller).setVisible(true);
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Critical Save Error: " + ex.getMessage());
        }
    }

    private void handleExit() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Desea salir de la ventana actual? Los cambios no se guardarán.",
                "Confirm Exit", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            new ParkingManagementFrame(currentUser, controller).setVisible(true);
            this.dispose();
        }
    }

    private void updateUI() {
        int totalRemaining = controller.getRemainingSpaces();
        int prefMissing = controller.getPrefRemaining();

        lblRemaining.setText("<html>Physical spaces left: <b>" + totalRemaining + "</b><br>"
                + "Pending preferential quota: <b>" + prefMissing + "</b></html>");

        updateQtyCombo(totalRemaining);

        if (totalRemaining > 0 && totalRemaining == prefMissing) {
            chkIsPreferential.setSelected(true);
            chkIsPreferential.setEnabled(false);
        } else {
            chkIsPreferential.setEnabled(totalRemaining > 0);
        }

        btnFinish.setEnabled(totalRemaining > 0 || controller.isConfigFinished());
    }

    private void updateQtyCombo(int max) {
        comboQtyToConfig.removeAllItems();
        for (int i = 1; i <= max; i++) {
            comboQtyToConfig.addItem(i);
        }
    }
}
