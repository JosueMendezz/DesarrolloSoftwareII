package view;

import controller.UserController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UserManagementFrame extends JFrame {

    private JTextField txtUser = new JTextField(15);
    private JPasswordField txtPass = new JPasswordField(15);
    private JComboBox<String> comboRole = new JComboBox<>(new String[]{"ADMIN", "CLERK"});
    private JButton btnAdd = new JButton("Registrar Usuario");
    private JButton btnUpdate = new JButton("Actualizar Datos");
    private JButton btnDelete = new JButton("Eliminar Usuario");
    private JButton btnBack = new JButton("Volver al Menú");
    private JTable userTable;
    private DefaultTableModel tableModel;
    private UserController controller;

    public UserManagementFrame() {
        setTitle("Administración de Usuarios - J-Node System");
        setSize(700, 500);
        setLayout(new BorderLayout(10, 10));

        setupUI();
        this.controller = new UserController(this);

        try {
            controller.initUserLoad();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + e.getMessage());
        }

        setupActions();
        setLocationRelativeTo(null);
    }

    private void setupUI() {

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Datos del Usuario"));

        form.add(new JLabel("Nombre de Usuario:"));
        form.add(txtUser);
        form.add(new JLabel("Contraseña:"));
        form.add(txtPass);
        form.add(new JLabel("Rol / Puesto:"));
        form.add(comboRole);

        form.add(btnAdd);
        form.add(btnUpdate);
        form.add(btnDelete);
        form.add(btnBack);

        tableModel = new DefaultTableModel(new Object[]{"Usuario", "Contraseña", "Rol"}, 0);
        userTable = new JTable(tableModel);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(userTable), BorderLayout.CENTER);
    }

    private void setupActions() {

        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = userTable.getSelectedRow();
                if (row != -1) {
                    txtUser.setText(tableModel.getValueAt(row, 0).toString());
                    txtPass.setText(tableModel.getValueAt(row, 1).toString());
                    comboRole.setSelectedItem(tableModel.getValueAt(row, 2).toString());

                    txtUser.setEditable(false);
                    comboRole.setEnabled(true);
                }
            }
        });

        // REGISTER
        btnAdd.addActionListener(e -> {
            try {
                controller.processUserRegistration();
                JOptionPane.showMessageDialog(this, "Usuario registrado correctamente");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
            }
        });

        // UPDATE
        btnUpdate.addActionListener(e -> {
            try {
                controller.processUserUpdate();
                JOptionPane.showMessageDialog(this, "Información actualizada");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // DELETE
        btnDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    controller.processUserDeletion();
                    JOptionPane.showMessageDialog(this, "Usuario eliminado");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnBack.addActionListener(e -> this.dispose());
    }

    public String getUsername() {
        return txtUser.getText();
    }

    public String getPassword() {
        return new String(txtPass.getPassword());
    }

    public String getSelectedRole() {
        return comboRole.getSelectedItem().toString();
    }

    public JTable getTable() {
        return userTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public void clearFields() {
        txtUser.setText("");
        txtPass.setText("");
        txtUser.setEditable(true);
        userTable.clearSelection();
    }
}
