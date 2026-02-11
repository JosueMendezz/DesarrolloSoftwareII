package view;

import controller.ParkingController;
import model.entities.ParkingLot;
import model.entities.User;
import javax.swing.*;
import java.awt.*;

public class ParkingCreateFrame extends JFrame {

    private final JTextField txtName = new JTextField(20);
    private final JSpinner spinTotalSpaces = new JSpinner(new SpinnerNumberModel(1, 1, 500, 1));
    private final JSpinner spinPreferential = new JSpinner(new SpinnerNumberModel(0, 0, 500, 1));

    private final JButton btnExit = new JButton("Salir");
    private final JButton btnBack = new JButton("Atrás");
    private final JButton btnNext = new JButton("Siguiente");

    private final User currentUser;
    private final ParkingLot parkingToEdit;
    private final ParkingController controller;

    public ParkingCreateFrame(User user, ParkingController controller) {
        this.currentUser = user;
        this.controller = controller;
        this.parkingToEdit = null;
        setupLayout();
        setTitle("J-Node - Create New Parking Lot");
    }

    public ParkingCreateFrame(User user, ParkingController controller, ParkingLot existingParking) {
        this.currentUser = user;
        this.controller = controller;
        this.parkingToEdit = existingParking;

        setupLayout();
        loadExistingData();
        setTitle("J-Node - Edit Branch: " + existingParking.getName());
        btnNext.setText("Modificar Espacios");
    }

    private void setupLayout() {
        setSize(450, 300);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelForm = new JPanel(new GridLayout(3, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panelForm.add(new JLabel("Nombre del Parqueo:"));
        panelForm.add(txtName);
        panelForm.add(new JLabel("Capacidad del parqueo:"));
        panelForm.add(spinTotalSpaces);
        panelForm.add(new JLabel("Espacios Preferenciales:"));
        panelForm.add(spinPreferential);

        add(panelForm, BorderLayout.CENTER);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButtons.add(btnExit);
        panelButtons.add(btnBack);
        panelButtons.add(btnNext);
        add(panelButtons, BorderLayout.SOUTH);

        setupListeners();
    }

    private void loadExistingData() {
        if (parkingToEdit != null) {
            txtName.setText(parkingToEdit.getName());
            spinTotalSpaces.setValue(parkingToEdit.getNumberOfSpaces());
            spinPreferential.setValue(parkingToEdit.getPreferentialSpaces());
        }
    }

    private void setupListeners() {
        btnNext.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();

                if (name.isEmpty()) {
                    throw new Exception("El Nombre del parqueo no puede quedar vacío.");
                }

                int total = ((Number) spinTotalSpaces.getValue()).intValue();
                int pref = ((Number) spinPreferential.getValue()).intValue();

                if (parkingToEdit == null || !parkingToEdit.getName().equalsIgnoreCase(name)) {
                    controller.validateParkingName(name);
                }

                if (pref > total) {
                    throw new Exception("Los espacios preferenciales no pueden exceder la cantidad de espacios totales.");
                }

                String originalName = (parkingToEdit != null) ? parkingToEdit.getName() : null;

                controller.prepareTempParking(name, total, pref);

                new SpaceConfigFrame(controller, currentUser, originalName).setVisible(true);
                this.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnBack.addActionListener(e -> {
            this.dispose();
            new ParkingManagementFrame(currentUser, controller).setVisible(true);
        });

        btnExit.addActionListener(e -> {
            this.dispose();
            new Dashboard(currentUser, controller.getFileManager()).setVisible(true);
        });
    }
}
