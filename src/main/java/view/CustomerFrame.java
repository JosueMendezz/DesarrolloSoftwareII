package view;

import controller.CustomerController; // Import required to instantiate the controller
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CustomerFrame extends JFrame {

    private JTextField txtId = new JTextField(15);
    private JTextField txtName = new JTextField(15);
    private JCheckBox chkPreferential = new JCheckBox("¿Espacio Preferencial?");
    private JButton registerButton = new JButton("Registrar");
    private JButton updateButton = new JButton("Modificar");
    private JButton deleteButton = new JButton("Eliminar");
    private JButton backButton = new JButton("Volver");
    private JTable customerTable;
    private DefaultTableModel customerTableModel;

    // Controller instance
    private CustomerController controller;

    public CustomerFrame() {
        setTitle("Gestión de Clientes - J-Node System");
        setSize(600, 500);
        setLayout(new BorderLayout(10, 10));

        setupComponents();

        this.controller = new CustomerController(this);

        try {
            controller.initDataLoad();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }

        setupListeners();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void setupComponents() {
        // Form Panel
        JPanel panelForm = new JPanel(new GridLayout(5, 1, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));
        panelForm.add(new JLabel("ID / Cédula:"));
        panelForm.add(txtId);
        panelForm.add(new JLabel("Nombre:"));
        panelForm.add(txtName);
        panelForm.add(chkPreferential);

        // Buttons Panel
        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(registerButton);
        panelButtons.add(updateButton);
        panelButtons.add(deleteButton);
        panelButtons.add(backButton);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(panelForm, BorderLayout.CENTER);
        topContainer.add(panelButtons, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // Table setup
        customerTableModel = new DefaultTableModel(new Object[]{"ID", "Nombre", "Preferencial"}, 0);
        customerTable = new JTable(customerTableModel);
        add(new JScrollPane(customerTable), BorderLayout.CENTER);
    }

    private void setupListeners() {
        // Register button logic
        registerButton.addActionListener(e -> {
            try {
                controller.processRegistration();
                JOptionPane.showMessageDialog(this, "Cliente registrado exitosamente");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Update button logic
        updateButton.addActionListener(e -> {
            try {
                controller.processUpdate();
                JOptionPane.showMessageDialog(this, "Cliente actualizado exitosamente");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Delete button logic
        deleteButton.addActionListener(e -> {
            try {
                controller.processDeletion();
                JOptionPane.showMessageDialog(this, "Cliente eliminado exitosamente");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> this.dispose());
    }

    // Getters for controller access
    public String getId() {
        return txtId.getText();
    }

    public String getName() {
        return txtName.getText();
    }

    public boolean isPreferential() {
        return chkPreferential.isSelected();
    }

    public JTable getTable() {
        return customerTable;
    }

    public DefaultTableModel getTableModel() {
        return customerTableModel;
    }

    public void clearFields() {
        txtId.setText("");
        txtName.setText("");
        chkPreferential.setSelected(false);
        txtId.setEditable(true);
    }
}
