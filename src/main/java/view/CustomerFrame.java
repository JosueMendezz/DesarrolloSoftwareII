package view;

import controller.CustomerController;
import model.data.FileManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import model.entities.User;

/**
 * View for Customer Management.
 * Implements clean separation: no business logic, only UI events and error display.
 */
public class CustomerFrame extends JFrame {

    private final JTextField txtId = new JTextField(15);
    private final JTextField txtName = new JTextField(15);
    private final JCheckBox chkPreferential = new JCheckBox("Espacio Preferencial?");
    private final JButton registerButton = new JButton("Registrar");
    private final JButton updateButton = new JButton("Modificar");
    private final JButton deleteButton = new JButton("Eliminar");
    private final JButton btnExit = new JButton("Salir");
    
    private JTable customerTable;
    private DefaultTableModel tableModel;
    
    private final User currentUser;
    private final CustomerController controller;

    public CustomerFrame(User user, CustomerController controller) {
        this.currentUser = user;
        this.controller = controller;
        
        setupConfiguration();
        setupComponents();
        setupListeners();
        initialDataLoad();
    }

    private void setupConfiguration() {
        setTitle("Customer Management - J-Node System");
        setSize(600, 500);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void setupComponents() {
        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Credenciales del cliente"));
        formPanel.add(new JLabel("ID:"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Nombre Completo:"));
        formPanel.add(txtName);
        formPanel.add(chkPreferential);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(registerButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(btnExit);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(formPanel, BorderLayout.CENTER);
        topContainer.add(buttonPanel, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // Table setup
        String[] columns = {"ID", "Nombre", "Preferencial"};
        tableModel = new DefaultTableModel(columns, 0);
        customerTable = new JTable(tableModel);
        add(new JScrollPane(customerTable), BorderLayout.CENTER);
    }

    private void setupListeners() {
        registerButton.addActionListener(e -> handleRegistration());
        updateButton.addActionListener(e -> handleUpdate());
        deleteButton.addActionListener(e -> handleDelete());
        btnExit.addActionListener(e -> {
    this.dispose();
    new Dashboard(currentUser, controller.getFileManager()).setVisible(true);
});
        
        // Selection listener to fill fields when a row is clicked
        customerTable.getSelectionModel().addListSelectionListener(e -> fillFieldsFromSelectedRow());
    }

    private void handleRegistration() {
        try {
            // CORRECCIÓN: Se eliminó el getTableDataList() porque el controlador ya maneja los datos internos.
            controller.registerCustomer(
                txtId.getText(), 
                txtName.getText(), 
                chkPreferential.isSelected()
            );
            
            refreshTable();
            clearFields();
            JOptionPane.showMessageDialog(this, "El cliente ha sido registrado exitosamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        try {
            int selectedRow = customerTable.getSelectedRow();
            // CORRECCIÓN: Se mantiene getTableDataList para actualizar el archivo completo basado en el estado de la tabla.
            controller.updateCustomer(
                selectedRow, 
                txtName.getText(), 
                chkPreferential.isSelected(), 
                getTableDataList()
            );
            
            refreshTable();
            clearFields();
            JOptionPane.showMessageDialog(this, "Cliente actualizado exitosamente.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        try {
            int selectedRow = customerTable.getSelectedRow();
            // CORRECCIÓN: Se mantiene getTableDataList para sincronizar el borrado.
            controller.removeCustomer(selectedRow, getTableDataList());
            
            refreshTable();
            clearFields();
            JOptionPane.showMessageDialog(this, "Cliente eliminado de la base de datos.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initialDataLoad() {
        try {
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Initial load failed: " + e.getMessage());
        }
    }

    private void refreshTable() throws Exception {
        tableModel.setRowCount(0);
        List<String[]> data = controller.getAllCustomers();
        for (String[] row : data) {
            // UI translation of data: "true" -> "Yes"
            String prefLabel = row[2].equalsIgnoreCase("true") ? "Si" : "No";
            tableModel.addRow(new Object[]{row[0], row[1], prefLabel});
        }
    }

    /**
     * Helper to extract current table state into a pure List for the Controller.
     * Used for update/delete synchronization.
     */
    private List<String[]> getTableDataList() {
        List<String[]> data = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String id = tableModel.getValueAt(i, 0).toString();
            String name = tableModel.getValueAt(i, 1).toString();
            String pref = tableModel.getValueAt(i, 2).toString().equals("Si") ? "true" : "false";
            data.add(new String[]{id, name, pref});
        }
        return data;
    }

    private void fillFieldsFromSelectedRow() {
        int row = customerTable.getSelectedRow();
        if (row >= 0) {
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtName.setText(tableModel.getValueAt(row, 1).toString());
            chkPreferential.setSelected(tableModel.getValueAt(row, 2).toString().equals("Si"));
            txtId.setEditable(false); // ID should not be changed during update
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        chkPreferential.setSelected(false);
        txtId.setEditable(true);
        customerTable.clearSelection();
    }
}