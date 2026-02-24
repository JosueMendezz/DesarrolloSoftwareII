package view;

import controller.ParkingController;
import model.entities.User;
import javax.swing.*;
import java.awt.*;

public class SpaceConfigFrame extends BaseFrame {

    private final JLabel lblRemaining = new JLabel();
    private final JComboBox<Integer> comboQtyToConfig = new JComboBox<>();
    private final JCheckBox chkIsPreferential = new JCheckBox("Espacio(s) Preferencial(es)");
    private final JComboBox<String> comboVehicleType = new JComboBox<>(
            new String[]{"Automóvil", "Motocicleta", "Bicicleta", "Vehículo Pesado"}
    );

    private final JButton btnExit = new JButton("Salir");
    private final JButton btnBack = new JButton("Atrás");
    private final JButton btnFinish = new JButton("Configurar Bloque");

    private final ParkingController controller;
    private final User currentUser;
    private final String oldName;

    public SpaceConfigFrame(ParkingController controller, User user, String originalName) {
        super("HEAP HAVEN - CONFIGURACIÓN DE ESPACIOS", 500, 500);
        this.controller = controller;
        this.currentUser = user;
        this.oldName = originalName;

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);
        this.setupCustomTitleBar("CONFIGURACIÓN DE INFRAESTRUCTURA");

        setupComponents();
        setupListeners();
        updateUI();

        setVisible(true);
    }

    private void setupComponents() {
        JPanel mainContent = new JPanel(new BorderLayout(15, 15));
        mainContent.setOpaque(false);
        mainContent.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 60, 60)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        lblRemaining.setForeground(Color.WHITE);
        lblRemaining.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoPanel.add(lblRemaining, BorderLayout.CENTER);
        JPanel panelForm = new JPanel(new GridLayout(3, 1, 20, 20));
        panelForm.setOpaque(false);
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panelForm.add(createFieldGroup("CANTIDAD DE ESPACIOS A ASIGNAR", comboQtyToConfig));
        chkIsPreferential.setOpaque(false);
        chkIsPreferential.setForeground(COLOR_CELESTE);
        chkIsPreferential.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkIsPreferential.setFocusPainted(false);
        panelForm.add(chkIsPreferential);

        panelForm.add(createFieldGroup("TIPO DE VEHÍCULO ADMITIDO", comboVehicleType));
        mainContent.add(infoPanel, BorderLayout.NORTH);
        mainContent.add(panelForm, BorderLayout.CENTER);
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelButtons.setOpaque(false);
        styleButton(btnExit, false);
        styleButton(btnBack, false);
        styleButton(btnFinish, true);
        panelButtons.add(btnExit);
        panelButtons.add(btnBack);
        panelButtons.add(btnFinish);
        mainContent.add(panelButtons, BorderLayout.SOUTH);
        getContentPane().add(mainContent, BorderLayout.CENTER);
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
            if (comboQtyToConfig.getSelectedItem() == null) {
                return;
            }

            int qty = (int) comboQtyToConfig.getSelectedItem();
            String type = (String) comboVehicleType.getSelectedItem();
            boolean isPref = chkIsPreferential.isSelected();

            int totalCap = controller.getTempParking().getNumberOfSpaces();
            int remaining = controller.getRemainingSpaces();
            int startRange = (totalCap - remaining) + 1;
            int endRange = startRange + qty - 1;

            if (oldName != null) {
                controller.validateSpaceBlockIsFree(oldName, startRange, endRange, type, isPref);
            }

            controller.configureSpaceBlock(qty, isPref, type);

            if (controller.isConfigFinished()) {
                finalizeParkingSetup();
            } else {
                JOptionPane.showMessageDialog(this, "Bloque configurado con éxito. Continúe con los restantes.");
                updateUI();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Integridad", JOptionPane.ERROR_MESSAGE);
            updateUI();
        }
    }

    private void finalizeParkingSetup() {
        try {
            controller.saveParkingConfiguration(oldName);
            JOptionPane.showMessageDialog(this, "¡Estructura guardada y archivos generados!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            new ParkingManagementFrame(currentUser, controller).setVisible(true);
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error crítico al guardar: " + ex.getMessage());
        }
    }

    private void handleExit() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea cancelar la configuración? Perderá los bloques definidos en esta sesión.",
                "Confirmar salida", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            new ParkingManagementFrame(currentUser, controller).setVisible(true);
            this.dispose();
        }
    }

    private void updateUI() {
        int totalRemaining = controller.getRemainingSpaces();
        int prefMissing = controller.getPrefRemaining();

        lblRemaining.setText("<html><body style='width: 300px;'>"
                + "Espacios totales por configurar: <b style='color:#00BFFF;'>" + totalRemaining + "</b><br>"
                + "Cupos preferenciales pendientes: <b style='color:#00BFFF;'>" + prefMissing + "</b>"
                + "</body></html>");

        updateQtyCombo(totalRemaining);

        if (totalRemaining > 0 && totalRemaining == prefMissing) {
            chkIsPreferential.setSelected(true);
            chkIsPreferential.setEnabled(false);
        } else {
            chkIsPreferential.setEnabled(totalRemaining > 0);
        }

        btnFinish.setText(controller.isConfigFinished() ? "Finalizar y Guardar" : "Configurar Bloque");
        btnFinish.setEnabled(totalRemaining > 0 || controller.isConfigFinished());
    }

    private void updateQtyCombo(int max) {
        comboQtyToConfig.removeAllItems();
        for (int i = 1; i <= max; i++) {
            comboQtyToConfig.addItem(i);
        }
    }

    private JPanel createFieldGroup(String labelText, JComboBox<?> combo) {
        JPanel group = new JPanel(new BorderLayout(5, 5));
        group.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(new Color(150, 150, 150));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        combo.setBackground(COLOR_ACCENTO);
        combo.setForeground(Color.WHITE);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ((JLabel) combo.getRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
        combo.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        group.add(lbl, BorderLayout.NORTH);
        group.add(combo, BorderLayout.CENTER);
        return group;
    }

    private void styleButton(JButton btn, boolean primary) {
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(130, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        if (primary) {
            btn.setBackground(COLOR_CELESTE);
            btn.setForeground(COLOR_FONDO);
        } else {
            btn.setBackground(new Color(60, 60, 60));
            btn.setForeground(Color.WHITE);
        }
        btn.setBorder(BorderFactory.createLineBorder(btn.getBackground().darker()));
    }
}
