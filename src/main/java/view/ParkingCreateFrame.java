package view;

import controller.ParkingController;
import model.entities.ParkingLot;
import model.entities.User;
import javax.swing.*;
import java.awt.*;

public class ParkingCreateFrame extends BaseFrame {

    private final JTextField txtName = new JTextField(20);
    private final JSpinner spinTotalSpaces = new JSpinner(new SpinnerNumberModel(1, 1, 500, 1));
    private final JSpinner spinPreferential = new JSpinner(new SpinnerNumberModel(0, 0, 500, 1));
    private final JButton btnBack = new JButton("Atrás");
    private final JButton btnNext = new JButton("Siguiente");
    private final User currentUser;
    private final ParkingLot parkingToEdit;
    private final ParkingController controller;

    public ParkingCreateFrame(User user, ParkingController controller) {
        super("HEAP HAVEN - CREAR NUEVO PARQUEO", 450, 420);
        this.currentUser = user;
        this.controller = controller;
        this.parkingToEdit = null;
        initCustomView("CREAR NUEVA SEDE");
    }

    public ParkingCreateFrame(User user, ParkingController controller, ParkingLot existingParking) {
        super("HEAP HAVEN - EDITAR SEDE", 450, 420);
        this.currentUser = user;
        this.controller = controller;
        this.parkingToEdit = existingParking;
        initCustomView("EDITAR SEDE: " + existingParking.getName().toUpperCase());
        loadExistingData();
        btnNext.setText("Modificar Espacios");
    }

    private void initCustomView(String titleText) {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);
        this.setupCustomTitleBar(titleText);
        setupLayout();
        setVisible(true);
    }

    private void setupLayout() {
        JPanel mainContent = new JPanel(new BorderLayout(15, 15));
        mainContent.setOpaque(false);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        JPanel panelForm = new JPanel(new GridLayout(3, 1, 15, 15));
        panelForm.setOpaque(false);
        panelForm.add(createFieldGroup("NOMBRE DE LA SEDE", txtName));
        panelForm.add(createFieldGroup("CAPACIDAD TOTAL", spinTotalSpaces));
        panelForm.add(createFieldGroup("CUPOS PREFERENCIALES", spinPreferential));
        mainContent.add(panelForm, BorderLayout.CENTER);
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelButtons.setOpaque(false);
        styleButton(btnBack, false);
        styleButton(btnNext, true);
        panelButtons.add(btnBack);
        panelButtons.add(btnNext);
        mainContent.add(panelButtons, BorderLayout.SOUTH);
        getContentPane().add(mainContent, BorderLayout.CENTER);
        setupListeners();
    }
    
    private JPanel createFieldGroup(String labelText, JComponent component) {
        JPanel group = new JPanel(new BorderLayout(5, 5));
        group.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setForeground(new Color(150, 150, 150)); 
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));

        if (component instanceof JTextField) {
            styleTextField((JTextField) component);
        } else if (component instanceof JSpinner) {
            styleSpinner((JSpinner) component);
        }

        group.add(lbl, BorderLayout.NORTH);
        group.add(component, BorderLayout.CENTER);
        return group;
    }

    private void styleTextField(JTextField tf) {
        tf.setBackground(COLOR_ACCENTO);
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(COLOR_CELESTE);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    }

    private void styleSpinner(JSpinner sp) {
        sp.setBackground(COLOR_ACCENTO);
        JComponent editor = sp.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setBackground(COLOR_ACCENTO);
            tf.setForeground(Color.WHITE);
            tf.setCaretColor(COLOR_CELESTE);
            tf.setBorder(BorderFactory.createEmptyBorder());
        }
        sp.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
    }

    private void styleButton(JButton btn, boolean primary) {
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(110, 35));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        if (primary) {
            btn.setBackground(COLOR_CELESTE); 
            btn.setForeground(COLOR_FONDO);  
            btn.setBorder(BorderFactory.createLineBorder(COLOR_CELESTE));
        } else {
            btn.setBackground(new Color(60, 60, 60));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        }
    }

    private void loadExistingData() {
        if (parkingToEdit != null) {
            txtName.setText(parkingToEdit.getName());
            spinTotalSpaces.setValue(parkingToEdit.getNumberOfSpaces());
            spinPreferential.setValue(parkingToEdit.getPreferentialSpaces());
        }
    }

    private void setupListeners() {
        btnBack.addActionListener(e -> {
            new ParkingManagementFrame(currentUser, controller).setVisible(true);
            this.dispose();
        });
        btnNext.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                int total = (int) spinTotalSpaces.getValue();
                int pref = (int) spinPreferential.getValue();

                if (name.isEmpty()) {
                    throw new Exception("El nombre no puede estar vacío.");
                }
                if (pref > total) {
                    throw new Exception("Cupos preferenciales exceden la capacidad.");
                }

                String nameInDatabase = (parkingToEdit != null) ? parkingToEdit.getName() : name;

                if (parkingToEdit != null) {
                    controller.validatePreferentialQuota(nameInDatabase, pref);
                }

                int ocupados = controller.getOccupancyCount(nameInDatabase);
                if (total < ocupados) {
                    throw new Exception("Capacidad insuficiente. Hay " + ocupados + " vehículos.");
                }

                String originalName = (parkingToEdit != null) ? parkingToEdit.getName() : null;
                controller.prepareTempParking(name, total, pref);

                new SpaceConfigFrame(controller, currentUser, originalName).setVisible(true);
                this.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
