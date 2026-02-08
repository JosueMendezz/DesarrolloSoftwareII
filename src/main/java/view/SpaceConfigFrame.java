/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import controller.ParkingController;
import javax.swing.*;
import java.awt.*;
import model.data.FileManager;
import model.entities.User;

/**
 *
 * @author Caleb Murillo
 */
public class SpaceConfigFrame extends JFrame {

    private JLabel lblRemaining = new JLabel("Espacios restantes: 0");
    private JComboBox<Integer> comboQtyToConfig = new JComboBox<>();
    private JCheckBox chkIsPreferential = new JCheckBox("¿Espacio(s) Preferencial(es)?");
    private JComboBox<String> comboVehicleType = new JComboBox<>(new String[]{"Car", "Motorcycle", "Bicycle", "HeavyVehicle"});

    private JButton btnExit = new JButton("Salir");
    private JButton btnBack = new JButton("Atrás");
    private JButton btnFinish = new JButton("Finalizar");

    private ParkingController controller;
    private User currentUser;
    private String oldName;

    public SpaceConfigFrame(ParkingController controller, User user, String originalName) {
        this.controller = controller;
        this.currentUser = user;
        this.oldName = originalName;

        setTitle("J-Node - Configuración de Espacios");
        setSize(450, 350);
        setLayout(new BorderLayout(15, 15));
        setLocationRelativeTo(null);

        JPanel panelMain = new JPanel(new GridLayout(5, 1, 10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        panelMain.add(lblRemaining);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row1.add(new JLabel("Cantidad a configurar:"));
        row1.add(comboQtyToConfig);
        panelMain.add(row1);

        panelMain.add(chkIsPreferential);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row2.add(new JLabel("Tipo de Vehículo:"));
        row2.add(comboVehicleType);
        panelMain.add(row2);

        add(panelMain, BorderLayout.CENTER);

        // Panel de Navegación
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButtons.add(btnExit);
        panelButtons.add(btnBack);
        panelButtons.add(btnFinish);
        add(panelButtons, BorderLayout.SOUTH);

        btnFinish.addActionListener(e -> {
            try {
                int qty = getSelectedQty();
                String type = getVehicleType();
                boolean isPref = isPreferential();

                controller.configureSpaceBlock(qty, isPref, type);

                if (controller.isConfigFinished()) {
                    int prefMissing = controller.getPrefRemaining();

                    if (prefMissing > 0) {
                        JOptionPane.showMessageDialog(this,
                                "¡Cuota Incompleta! Faltan " + prefMissing + " espacios de discapacidad.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        
                        FileManager.updateParkingInFile(controller.getTempParking(), oldName);

                        JOptionPane.showMessageDialog(this, "¡Sede actualizada correctamente!");
                        new ParkingManagementFrame(currentUser).setVisible(true);
                        this.dispose();
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Bloque configurado. Restan " + controller.getRemainingSpaces());
                }
                updateUI();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                updateUI();
            }
        });
        int iniciales = controller.getRemainingSpaces(); // El total que definimos antes
        setRemainingText(iniciales);
        updateQtyCombo(iniciales);
        chkIsPreferential.addActionListener(e -> updateUI());
        updateUI();
        
        // Botón Atrás: Regresa a la pantalla de datos básicos (Nombre/Capacidad)
        btnBack.addActionListener(e -> {
            // Reabrimos la ventana de creación/edición pasando el controlador actual
            // para que no se pierda el progreso de lo que ya se escribió.
            new ParkingCreateFrame(currentUser, controller.getTempParking()).setVisible(true);
            this.dispose();
        });

        // Botón Salir: Cancela todo y vuelve a la gestión de sedes
        btnExit.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Estás seguro de salir? Se perderán los cambios no finalizados.", 
                "Confirmar Salida", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                new ParkingManagementFrame(currentUser).setVisible(true);
                this.dispose();
            }
        });
    }// fin del constructor

    // Métodos para actualizar la UI dinámicamente
    public void setRemainingText(int count) {
        lblRemaining.setText("Espacios restantes: " + count);
    }

    public void updateQtyCombo(int max) {
        comboQtyToConfig.removeAllItems();
        for (int i = 1; i <= max; i++) {
            comboQtyToConfig.addItem(i);
        }
    }

    // Getters
    public int getSelectedQty() {
        return (int) comboQtyToConfig.getSelectedItem();
    }

    public boolean isPreferential() {
        return chkIsPreferential.isSelected();
    }

    public String getVehicleType() {
        return (String) comboVehicleType.getSelectedItem();
    }

    private void updateUI() {
        int totalRemaining = controller.getRemainingSpaces();
        int prefMissing = controller.getPrefRemaining();

        // Mostramos la realidad sin adornos
        lblRemaining.setText("<html>Espacios físicos restantes: <b>" + totalRemaining + "</b><br>"
                + "Cuota de discapacidad pendiente: <b>" + prefMissing + "</b></html>");

        // EL CAMBIO CLAVE: Siempre mostramos el máximo de espacios físicos disponibles.
        // No bloqueamos el combo aquí, lo validamos al darle "Finalizar".
        updateQtyCombo(totalRemaining);
    }
}
    