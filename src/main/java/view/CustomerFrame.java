package view;

import controller.CustomerController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.TitledBorder;
import model.entities.User;

public class CustomerFrame extends BaseFrame {

    private final JTextField txtId;
    private final JTextField txtName;
    private final JCheckBox chkPreferential;
    private final JButton registerButton;
    private final JButton updateButton;
    private final JButton deleteButton;
    private final JButton btnExit;
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private final User currentUser;
    private final CustomerController controller;

    public CustomerFrame(User user, CustomerController controller) {
        super("HEAP HAVEN - GESTIÓN DE CLIENTES", 700, 600);
        this.currentUser = user;
        this.controller = controller;
        txtId = createStyledTextField();
        txtName = createStyledTextField();
        chkPreferential = new JCheckBox("¿APLICA ESPACIO PREFERENCIAL?");
        styleCheckbox(chkPreferential);
        registerButton = createStyledButton("Registrar", true);
        updateButton = createStyledButton("Modificar", false);
        deleteButton = createStyledButton("Eliminar", false);
        btnExit = createStyledButton("Volver", false);
        getContentPane().setLayout(new BorderLayout());
        this.setupCustomTitleBar("HEAP HAVEN - GESTIÓN DE CLIENTES");
        setupComponents();
        setupListeners();
        initialDataLoad();
        this.getRootPane().setDefaultButton(registerButton);
        setVisible(true);
    }

    private void setupComponents() {
        JPanel mainContent = new JPanel(new BorderLayout(15, 15));
        mainContent.setOpaque(false);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_CELESTE, 1), " DATOS DEL CLIENTE ");
        border.setTitleColor(COLOR_CELESTE);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("IDENTIFICACIÓN:"), gbc);
        gbc.gridy = 1;
        formPanel.add(txtId, gbc);
        gbc.gridy = 2;
        formPanel.add(createLabel("NOMBRE COMPLETO:"), gbc);
        gbc.gridy = 3;
        formPanel.add(txtName, gbc);
        gbc.gridy = 4;
        formPanel.add(chkPreferential, gbc);
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.add(registerButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(btnExit);
        JPanel topContainer = new JPanel(new BorderLayout(20, 0));
        topContainer.setOpaque(false);
        topContainer.add(formPanel, BorderLayout.CENTER);
        topContainer.add(buttonPanel, BorderLayout.EAST);
        mainContent.add(topContainer, BorderLayout.NORTH);
        String[] columns = {"ID", "NOMBRE COMPLETO", "PREFERENCIAL"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        customerTable = new JTable(tableModel);
        applyCustomTableStyle(customerTable);

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.getViewport().setBackground(COLOR_FONDO);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        getContentPane().add(mainContent, BorderLayout.CENTER);
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
            txtId.setEditable(false);
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        chkPreferential.setSelected(false);
        txtId.setEditable(true);
        customerTable.clearSelection();
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(COLOR_TEXTO);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return lbl;
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField(15);
        tf.setBackground(COLOR_ACCENTO);
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(COLOR_CELESTE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return tf;
    }

    private void styleCheckbox(JCheckBox cb) {
        cb.setOpaque(false);
        cb.setForeground(COLOR_TEXTO);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cb.setFocusPainted(false);
    }
}
