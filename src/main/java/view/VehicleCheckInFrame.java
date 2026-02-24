package view;

import controller.VehicleController;
import model.entities.User;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import model.entities.Customer;
import java.util.List;
import javax.swing.border.TitledBorder;

public class VehicleCheckInFrame extends BaseFrame {

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
    private boolean isCustomerVerified = false;
    private final JButton btnViewConfig = new JButton("Ver Distribución");

    public VehicleCheckInFrame(User user, VehicleController controller) {
        super("HEAP HAVEN - CHECK-IN DE VEHÍCULOS", 650, 850);
        this.currentUser = user;
        this.controller = controller;
        getContentPane().setLayout(new BorderLayout());
        this.setupCustomTitleBar("HEAP HAVEN - CHECK-IN DE VEHÍCULOS");
        setupComponents();
        setupListeners();
        loadParkingLots();
        this.getRootPane().setDefaultButton(btnRegister);
        setVisible(true);
    }

    private void setupComponents() {
        JPanel contentScrollable = new JPanel();
        contentScrollable.setLayout(new BoxLayout(contentScrollable, BoxLayout.Y_AXIS));
        contentScrollable.setBackground(COLOR_FONDO);
        contentScrollable.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        TitledBorder customerBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70), 1), " CREDENCIALES DEL CLIENTE ");
        customerBorder.setTitleColor(COLOR_CELESTE);
        customerBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        TitledBorder vehicleBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70), 1), " ESPECIFICACIONES DEL VEHÍCULO ");
        vehicleBorder.setTitleColor(COLOR_CELESTE);
        styleTextField(txtOwnerId);
        styleTextField(txtOwnerName);
        styleCheckBox(chkIsPreferential);
        styleButton(btnCheckCustomer, false);
        styleButton(btnRegisterCustomer, false);
        styleButton(btnViewConfig, false);
        styleTextField(txtPlate);
        styleTextField(txtBrand);
        styleTextField(txtModel);
        styleTextField(txtColor);
        styleTextField(txtDetails);
        styleComboBox(comboVehicleType);
        styleTextField(txtOtherName);
        styleButton(btnAddOther, false);
        styleButton(btnBack, false);
        styleButton(btnRegister, true);
        styleComboBox(comboParking);
        JPanel pnlCustomer = new JPanel(new GridBagLayout());
        pnlCustomer.setOpaque(false);
        pnlCustomer.setBorder(BorderFactory.createCompoundBorder(customerBorder, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlCustomer.add(createLabel("ID CLIENTE:"), gbc);
        gbc.gridx = 1;
        pnlCustomer.add(txtOwnerId, gbc);
        gbc.gridx = 2;
        pnlCustomer.add(btnCheckCustomer, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        pnlCustomer.add(createLabel("NOMBRE COMPLETO:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        pnlCustomer.add(txtOwnerName, gbc);
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        gbc.gridx = 0;
        pnlCustomer.add(createLabel("ACCESO:"), gbc);
        gbc.gridx = 1;
        pnlCustomer.add(chkIsPreferential, gbc);
        gbc.gridx = 2;
        pnlCustomer.add(btnRegisterCustomer, gbc);
        JPanel pnlVehicle = new JPanel(new GridBagLayout());
        pnlVehicle.setOpaque(false);
        pnlVehicle.setBorder(BorderFactory.createCompoundBorder(vehicleBorder, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlVehicle.add(createLabel("PLACA:"), gbc);
        gbc.gridx = 1;
        pnlVehicle.add(txtPlate, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        pnlVehicle.add(createLabel("TIPO:"), gbc);
        gbc.gridx = 1;
        pnlVehicle.add(comboVehicleType, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        pnlVehicle.add(createLabel("MARCA / MODELO:"), gbc);
        JPanel pnlBrandModel = new JPanel(new GridLayout(1, 2, 5, 0));
        pnlBrandModel.setOpaque(false);
        pnlBrandModel.add(txtBrand);
        pnlBrandModel.add(txtModel);
        gbc.gridx = 1;
        pnlVehicle.add(pnlBrandModel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        pnlVehicle.add(createLabel("COLOR:"), gbc);
        gbc.gridx = 1;
        pnlVehicle.add(txtColor, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        pnlVehicle.add(createLabel("DETALLES (OPCIONAL):"), gbc);
        gbc.gridx = 1;
        pnlVehicle.add(txtDetails, gbc);
        JPanel pnlOthers = new JPanel(new BorderLayout(5, 5));
        pnlOthers.setOpaque(false);
        TitledBorder othersBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)), " OTROS AUTORIZADOS A RETIRAR EL VEHÍCULO ");
        othersBorder.setTitleColor(Color.GRAY);
        pnlOthers.setBorder(BorderFactory.createCompoundBorder(othersBorder, BorderFactory.createEmptyBorder(5, 10, 10, 10)));
        JPanel pnlInputOther = new JPanel(new BorderLayout(5, 0));
        pnlInputOther.setOpaque(false);
        pnlInputOther.add(txtOtherName, BorderLayout.CENTER);
        pnlInputOther.add(btnAddOther, BorderLayout.EAST);
        listOthers.setBackground(COLOR_ACCENTO);
        listOthers.setForeground(Color.WHITE);
        JScrollPane scrollOthers = new JScrollPane(listOthers);
        scrollOthers.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        scrollOthers.setPreferredSize(new Dimension(0, 80));
        pnlOthers.add(pnlInputOther, BorderLayout.NORTH);
        pnlOthers.add(scrollOthers, BorderLayout.CENTER);
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        pnlFooter.setOpaque(false);
        pnlFooter.add(btnBack);
        pnlFooter.add(createLabel("SEDE:"));
        pnlFooter.add(comboParking);
        pnlFooter.add(btnViewConfig);
        pnlFooter.add(btnRegister);
        contentScrollable.add(pnlCustomer);
        contentScrollable.add(Box.createRigidArea(new Dimension(0, 15)));
        contentScrollable.add(pnlVehicle);
        contentScrollable.add(Box.createRigidArea(new Dimension(0, 15)));
        contentScrollable.add(pnlOthers);
        contentScrollable.add(pnlFooter);
        JScrollPane mainScroll = new JScrollPane(contentScrollable);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        getContentPane().add(mainScroll, BorderLayout.CENTER);
    }

    private void setupListeners() {
        txtOwnerId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                isCustomerVerified = false;
                btnRegisterCustomer.setEnabled(false);
                lockCustomerFields(false);
                txtOwnerName.setText("");
            }
        });

        btnCheckCustomer.addActionListener(e -> {
            String id = txtOwnerId.getText().trim();
            if (id.isEmpty()) {
                showErrorMessage("Por favor ingrese el ID del cliente.");
                return;
            }
            try {
                Customer customer = controller.findCustomerById(id);
                if (customer != null) {
                    txtOwnerName.setText(customer.getName());
                    chkIsPreferential.setSelected(customer.isPreferential());
                    lockCustomerFields(true);
                    btnRegisterCustomer.setEnabled(false);
                    isCustomerVerified = true;
                    showInfoMessage("Cliente encontrado y verificado.");
                } else {
                    isCustomerVerified = false;
                    lockCustomerFields(false);
                    btnRegisterCustomer.setEnabled(true);
                    showInfoMessage("El ID es nuevo. Debe completar los datos y darle a 'Guardar nuevo cliente'.");
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
                    throw new Exception("El nombre es requerido para el registro.");
                }

                controller.registerNewOwner(id, name, isPref);
                lockCustomerFields(true);
                btnRegisterCustomer.setEnabled(false);
                isCustomerVerified = true;
                showInfoMessage("Cliente registrado y verificado correctamente.");
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
                showErrorMessage("Ingrese el nombre para añadir otra persona autorizada a retirar el vehículo.");
            }
        });

        btnViewConfig.addActionListener(e -> {
            String selectedSede = (String) comboParking.getSelectedItem();
            if (selectedSede == null || selectedSede.isEmpty()) {
                showErrorMessage("Por favor, seleccione una sede primero.");
                return;
            }
            controller.ParkingController parkingCtrl = new controller.ParkingController(controller.getFileManager());
            ParkingConfigView configView = new ParkingConfigView(this, null, parkingCtrl, selectedSede);
            configView.setVisible(true);
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
        isCustomerVerified = false;
    }

    private void handleVehicleEntry() {
        try {
            if (!isCustomerVerified) {
                throw new Exception("El cliente no ha sido verificado.\nUse 'Buscar ID' o 'Guardar nuevo cliente' antes de continuar.");
            }
            String ownerId = txtOwnerId.getText().trim();
            String plate = txtPlate.getText().trim();
            String brand = txtBrand.getText().trim();
            String model = txtModel.getText().trim();
            String color = txtColor.getText().trim();
            String parkingName = (String) comboParking.getSelectedItem();
            String selectedType = (String) comboVehicleType.getSelectedItem();
            boolean isClientPreferential = chkIsPreferential.isSelected();
            StringBuilder missingFields = new StringBuilder();
            if (plate.isEmpty()) {
                missingFields.append("- Placa\n");
            }
            if (brand.isEmpty()) {
                missingFields.append("- Marca\n");
            }
            if (model.isEmpty()) {
                missingFields.append("- Modelo\n");
            }
            if (color.isEmpty()) {
                missingFields.append("- Color\n");
            }
            if (parkingName == null || parkingName.isEmpty()) {
                missingFields.append("- Sede/Parqueo\n");
            }

            if (missingFields.length() > 0) {
                throw new Exception("Los siguientes campos son obligatorios:\n" + missingFields.toString());
            }
            if (controller.isVehicleAlreadyParked(plate)) {
                showErrorMessage("El vehículo con placa " + plate + " ya se encuentra en el sistema.");
                return;
            }
            List<String> extraResponsibles = new ArrayList<>();
            for (int i = 0; i < listModelOthers.size(); i++) {
                extraResponsibles.add(listModelOthers.getElementAt(i));
            }
            int assignedSpace = controller.processVehicleEntry(
                    parkingName, plate, selectedType,
                    brand, model, color,
                    txtDetails.getText().trim(), isClientPreferential,
                    extraResponsibles, ownerId
            );
            if (assignedSpace != -1) {
                showInfoMessage("Vehículo ingresado con éxito\nEspacio asignado: " + assignedSpace);
                clearFields();
                txtPlate.requestFocus();
            } else {
                showErrorMessage("No hay espacios disponibles para: " + selectedType
                        + "\n(Preferencial: " + (isClientPreferential ? "SÍ" : "NO") + ")");
            }

        } catch (Exception ex) {
            showErrorMessage(ex.getMessage());
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

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        return lbl;
    }

    private void styleTextField(JTextField tf) {
        tf.setBackground(COLOR_ACCENTO);
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(COLOR_CELESTE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
    }

    private void styleCheckBox(JCheckBox cb) {
        cb.setOpaque(false);
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    }

    private void styleButton(JButton btn, boolean primary) {
        btn.setFocusPainted(false);
        if (primary) {
            btn.setBackground(COLOR_CELESTE);
            btn.setForeground(COLOR_FONDO);
        } else {
            btn.setBackground(new Color(60, 60, 60));
            btn.setForeground(Color.WHITE);
        }
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
    }

    private void styleComboBox(JComboBox<?> cb) {
        Color grisMate = new Color(45, 45, 45);

        cb.setBackground(grisMate);
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        cb.setFocusable(false);
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {

                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (index == -1) {
                    lbl.setBackground(grisMate);
                    lbl.setForeground(Color.WHITE);
                } else {
                    if (isSelected) {
                        lbl.setBackground(COLOR_CELESTE);
                        lbl.setForeground(COLOR_FONDO);
                    } else {
                        lbl.setBackground(grisMate);
                        lbl.setForeground(Color.WHITE);
                    }
                }

                lbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return lbl;
            }
        });
        cb.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("▼"); // Usamos un caracter simple
                button.setFont(new Font("Arial", Font.PLAIN, 8));
                button.setFocusPainted(false);
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                button.setBackground(grisMate);
                button.setForeground(Color.GRAY);
                return button;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(grisMate);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });
    }
}
