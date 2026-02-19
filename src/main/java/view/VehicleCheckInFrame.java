package view;

import controller.VehicleController;
import model.entities.User;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import model.entities.Customer;
import java.util.List;

/**
 * @author Caleb Murillo
 */
public class VehicleCheckInFrame extends JFrame {

    private final JTextField txtOwnerId = new JTextField(15);
    private final JTextField txtOwnerName = new JTextField(15);
    private final JCheckBox chkIsPreferential = new JCheckBox("Presenta discapacidad");
    private final JButton btnCheckCustomer = new JButton("Buscar ID");
    private final JButton btnRegisterCustomer = new JButton("Guardar nuevo cliente");

    private final JTextField txtPlate = new JTextField(10);
    private final JTextField txtBrand = new JTextField(10);
    private final JTextField txtModel = new JTextField(10);
    private final JComboBox<String> comboVehicleType = new JComboBox<>(new String[]{"Automóvil", "Motocicleta", "Bicicleta", "Vehículo Pesado"});
    private final JTextField txtColor = new JTextField(10);
    private final JTextField txtDetails = new JTextField(20);

    private final DefaultListModel<String> listModelOthers = new DefaultListModel<>();
    private final JList<String> listOthers = new JList<>(listModelOthers);
    private final JTextField txtOtherName = new JTextField(15);
    private final JButton btnAddOther = new JButton("+");

    private final JComboBox<String> comboParking = new JComboBox<>();
    private final JButton btnRegister = new JButton("ingresar vehículo");
    private final JButton btnBack = new JButton("Menú principal");

    private final User currentUser;
    private final VehicleController controller;

    public VehicleCheckInFrame(User user, VehicleController controller) {
        this.currentUser = user;
        this.controller = controller;

        setupConfiguration();
        setupComponents();
        setupListeners();

        loadParkingLots();

        this.getRootPane().setDefaultButton(btnRegister);
    }

    private void setupConfiguration() {
        setTitle("Check-In de Vehiculos - J-Node System");
        setSize(650, 800);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void setupComponents() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JPanel pnlCustomer = new JPanel(new GridBagLayout());
        pnlCustomer.setBorder(BorderFactory.createTitledBorder("Credenciales del cliente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        pnlCustomer.add(new JLabel("ID cliente:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.4;
        pnlCustomer.add(txtOwnerId, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0.3;
        pnlCustomer.add(btnCheckCustomer, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        pnlCustomer.add(new JLabel("Nombre completo:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        pnlCustomer.add(txtOwnerName, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        pnlCustomer.add(new JLabel("tipo de acceso:"), gbc);
        gbc.gridx = 1;
        pnlCustomer.add(chkIsPreferential, gbc);
        gbc.gridx = 2;
        pnlCustomer.add(btnRegisterCustomer, gbc);

        JPanel pnlVehicle = new JPanel(new GridBagLayout());
        pnlVehicle.setBorder(BorderFactory.createTitledBorder("Credenciales del vehículo"));

        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlVehicle.add(new JLabel("Placa:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        pnlVehicle.add(txtPlate, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        pnlVehicle.add(new JLabel("Tipo de vehículo:"), gbc);
        gbc.gridx = 1;
        pnlVehicle.add(comboVehicleType, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        pnlVehicle.add(new JLabel("Marca/Modelo:"), gbc);
        JPanel pnlBrandModel = new JPanel(new GridLayout(1, 2, 5, 0));
        pnlBrandModel.add(txtBrand);
        pnlBrandModel.add(txtModel);
        gbc.gridx = 1;
        pnlVehicle.add(pnlBrandModel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        pnlVehicle.add(new JLabel("Color:"), gbc);
        gbc.gridx = 1;
        pnlVehicle.add(txtColor, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        pnlVehicle.add(new JLabel("Detalles (Opcional):"), gbc);
        gbc.gridx = 1;
        pnlVehicle.add(txtDetails, gbc);

        JPanel pnlOthers = new JPanel(new BorderLayout(5, 5));
        pnlOthers.setBorder(BorderFactory.createTitledBorder("Agregar responsable (Opcional)"));

        JScrollPane scrollOthers = new JScrollPane(listOthers);
        scrollOthers.setPreferredSize(new Dimension(0, 100));
        pnlOthers.add(scrollOthers, BorderLayout.CENTER);

        JPanel pnlInputOther = new JPanel(new BorderLayout(5, 0));
        pnlInputOther.add(txtOtherName, BorderLayout.CENTER);
        pnlInputOther.add(btnAddOther, BorderLayout.EAST);
        pnlOthers.add(pnlInputOther, BorderLayout.SOUTH);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pnlFooter.add(btnBack);
        pnlFooter.add(new JLabel("Parqueo:"));
        pnlFooter.add(comboParking);
        pnlFooter.add(btnRegister);

        mainPanel.add(pnlCustomer);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(pnlVehicle);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(pnlOthers);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(pnlFooter);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupListeners() {
        btnCheckCustomer.addActionListener(e -> {
            String id = txtOwnerId.getText().trim();
            if (id.isEmpty()) {
                showErrorMessage("Porfavor ingrese su id de cliente.");
                return;
            }
            try {
                Customer customer = controller.findCustomerById(id);
                if (customer != null) {
                    txtOwnerName.setText(customer.getName());
                    chkIsPreferential.setSelected(customer.isPreferential());
                    lockCustomerFields(true);
                    btnRegisterCustomer.setEnabled(false);
                    showInfoMessage("Cliente encontrado");
                } else {
                    txtOwnerName.setText("");
                    chkIsPreferential.setSelected(false);
                    lockCustomerFields(false);
                    btnRegisterCustomer.setEnabled(true);
                    showInfoMessage("ID es nuevo,complete los espacios para registrarlo.");
                }
            } catch (Exception ex) {
                showErrorMessage("Error: " + ex.getMessage());
            }
        });

        btnRegisterCustomer.addActionListener(e -> {
            try {
                String id = txtOwnerId.getText().trim();
                String name = txtOwnerName.getText().trim();
                boolean isPref = chkIsPreferential.isSelected();
                if (name.isEmpty()) {
                    throw new Exception("Nombre es requerido.");
                }
                controller.registerNewOwner(id, name, isPref);
                lockCustomerFields(true);
                btnRegisterCustomer.setEnabled(false);
                showInfoMessage("Cliente registrado.");
            } catch (Exception ex) {
                showErrorMessage(ex.getMessage());
            }
        });

        btnRegister.addActionListener(e -> handleVehicleEntry());

        btnBack.addActionListener(e -> {
            this.dispose();
            new view.Dashboard(currentUser, controller.getFileManager()).setVisible(true);
        });

        btnAddOther.addActionListener(e -> {
            String name = txtOtherName.getText().trim();
            if (!name.isEmpty()) {
                listModelOthers.addElement(name);
                txtOtherName.setText("");
                txtOtherName.requestFocus();
            } else {
                showErrorMessage("Ingrese el nombre para añadir responsable.");
            }
        });
    }

    private void clearFields() {
        txtPlate.setText("");
        txtBrand.setText("");
        txtModel.setText("");
        txtColor.setText("");
        txtDetails.setText("");
        txtOwnerId.setText("");
        txtOwnerName.setText("");

        chkIsPreferential.setSelected(false);
        if (comboVehicleType.getItemCount() > 0) {
            comboVehicleType.setSelectedIndex(0);
        }

        if (listModelOthers != null) {
            listModelOthers.clear();
        }

        txtPlate.requestFocus();
    }

    private void handleVehicleEntry() {
        try {
            String ownerId = txtOwnerId.getText().trim();
            String ownerName = txtOwnerName.getText().trim();
            boolean isClientPreferential = chkIsPreferential.isSelected();

            if (ownerId.isEmpty()) {
                throw new Exception("El ID del cliente es obligatorio.");
            }

            if (controller.customerExists(ownerId)) {

                if (txtOwnerName.isEditable()) {
                    Customer existing = controller.findCustomerById(ownerId);

                    JOptionPane.showMessageDialog(this,
                            "El ID " + ownerId + " ya existe. Se usarán los datos registrados: " + existing.getName(),
                            "Cliente ya registrado", JOptionPane.INFORMATION_MESSAGE);

                    txtOwnerName.setText(existing.getName());
                    chkIsPreferential.setSelected(existing.isPreferential());
                    isClientPreferential = existing.isPreferential(); // Sincronizamos la variable
                    lockCustomerFields(true);
                }
            } else {
                if (ownerName.isEmpty()) {
                    throw new Exception("El ID es nuevo, por favor ingrese el nombre del cliente.");
                }
                controller.registerNewOwner(ownerId, ownerName, isClientPreferential);
            }

            String plate = txtPlate.getText().trim();
            String color = txtColor.getText().trim();
            String parkingName = (String) comboParking.getSelectedItem();
            String selectedType = (String) comboVehicleType.getSelectedItem();

            isClientPreferential = chkIsPreferential.isSelected();

            if (plate.isEmpty() || color.isEmpty() || parkingName == null) {
                throw new Exception("Placa, color y Parqueo son espacios obligatorios.");
            }

            if (controller.isVehicleAlreadyParked(plate)) {
                showErrorMessage("Vehículo ya existe.");
                return;
            }

            List<String> extraResponsibles = new ArrayList<>();
            for (int i = 0; i < listModelOthers.size(); i++) {
                extraResponsibles.add(listModelOthers.getElementAt(i));
            }

            int assignedSpace = controller.processVehicleEntry(
                    parkingName, plate, selectedType,
                    txtBrand.getText().trim(), txtModel.getText().trim(), color,
                    txtDetails.getText().trim(), isClientPreferential,
                    extraResponsibles, txtOwnerId.getText().trim()
            );

            if (assignedSpace != -1) {
                showInfoMessage("Registrado exitosamente en el espacio: " + assignedSpace);

                clearFields();
                txtPlate.requestFocus();

            } else {
                showErrorMessage("No hay espacios disponibles para el tipo de vehículo: " + selectedType
                        + " (Preferencial: " + isClientPreferential + ")");
            }
        } catch (Exception ex) {
            showErrorMessage("Error: " + ex.getMessage());
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void lockCustomerFields(boolean lock) {
        txtOwnerName.setEditable(!lock);
        chkIsPreferential.setEnabled(!lock);
    }

    private void resetCustomerSection() {
        txtOwnerName.setText("");
        chkIsPreferential.setSelected(false);
        lockCustomerFields(false);
    }

    private void loadParkingLots() {
        try {
            comboParking.removeAllItems();

            if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
                List<String> parkingNames = controller.getAvailableParkingNames();
                for (String name : parkingNames) {
                    comboParking.addItem(name);
                }
            } else {
                String assignedSede = currentUser.getAssignedParking();
                comboParking.addItem(assignedSede);

                comboParking.setEnabled(false);
            }

        } catch (Exception ex) {
            showErrorMessage("Error al cargar el parqueo: " + ex.getMessage());
        }
    }
}
