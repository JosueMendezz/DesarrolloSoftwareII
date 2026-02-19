package view;

import controller.CustomerController;
import model.data.FileManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import model.entities.User;

public class CustomerFrame extends JFrame {

    private final JTextField txtId = new JTextField(15);
    private final JTextField txtName = new JTextField(15);
    private final JCheckBox chkPreferential = new JCheckBox("Espacio Preferencial?");
    private final JButton registerButton = new JButton("Registrar");
    private final JButton updateButton = new JButton("Modificar");
    private final JButton deleteButton = new JButton("Eliminar");
    private final JButton btnExit = new JButton("Volver");

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
        this.getRootPane().setDefaultButton(registerButton);
    }

    private void setupConfiguration() {
        setTitle("Customer Management - J-Node System");
        setSize(600, 500);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void setupComponents() {
        JPanel formPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Credenciales del cliente"));
        formPanel.add(new JLabel("ID:"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Nombre Completo:"));
        formPanel.add(txtName);
        formPanel.add(chkPreferential);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(registerButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(btnExit);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(formPanel, BorderLayout.CENTER);
        topContainer.add(buttonPanel, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        String[] columns = {"ID", "Nombre", "Preferencial"};
        tableModel = new DefaultTableModel(columns, 0);
        customerTable = new JTable(tableModel);
        add(new JScrollPane(customerTable), BorderLayout.CENTER);
    }

    private void setupListeners() {
        registerButton.addActionListener(e -> handleRegistration());
        updateButton.addActionListener(e -> handleUpdate());
        deleteButton.addActionListener(e -> handleDelete());

        btnExit.addActionListener(e -> handleBack());

        customerTable.getSelectionModel().addListSelectionListener(e -> fillFieldsFromSelectedRow());
    }

    private void handleBack() {
        if (customerTable.getSelectedRow() != -1) {
            customerTable.clearSelection();
            clearFields();
        } else {
            this.dispose();
            new Dashboard(currentUser, controller.getFileManager()).setVisible(true);
        }
    }

    private void handleRegistration() {
        try {
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
        int selectedRow = customerTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un cliente de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de que desea eliminar a este cliente? Esta acción no se puede deshacer.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.removeCustomer(selectedRow, getTableDataList());

                refreshTable();
                clearFields();
                JOptionPane.showMessageDialog(this, "Cliente eliminado de la base de datos.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void initialDataLoad() {
        try {
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "La carga inicial falló " + e.getMessage());
        }
    }

    private void refreshTable() throws Exception {
        tableModel.setRowCount(0);
        List<String[]> data = controller.getAllCustomers();
        for (String[] row : data) {
            String prefLabel = row[2].equalsIgnoreCase("true") ? "Si" : "No";
            tableModel.addRow(new Object[]{row[0], row[1], prefLabel});
        }
    }

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
            txtId.setEditable(false); // 
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
