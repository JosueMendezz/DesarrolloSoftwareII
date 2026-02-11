package view;

import controller.UserController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.entities.User;
import java.util.List;

/**
 * View for managing system users (ADMIN/CLERK). Clean Architecture: This class
 * is a passive UI that communicates with UserController.
 */
public class UserManagementFrame extends JFrame {

    private final JTextField txtUser = new JTextField(15);
    private final JPasswordField txtPass = new JPasswordField(15);
    private final JComboBox<String> comboRole = new JComboBox<>(new String[]{"ADMIN", "OPERATOR"});

    private final JButton btnAdd = new JButton("Registrar");
    private final JButton btnUpdate = new JButton("Modificar");
    private final JButton btnDelete = new JButton("Eliminar");
    private final JButton btnExit = new JButton("Salir");

    private JTable userTable;
    private DefaultTableModel tableModel;

    private final User currentUser;
    private final UserController controller;

    public UserManagementFrame(User user, UserController controller) {
        this.currentUser = user;
        this.controller = controller;

        setupConfiguration();
        setupComponents();
        setupListeners();
        refreshTableData();

        try {
            controller.loadInitialUsers();
        } catch (Exception e) {
            showErrorMessage("Error loading users: " + e.getMessage());
        }
    }

    private void setupConfiguration() {
        setTitle("User Administration - J-Node System");
        setSize(700, 500);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void setupComponents() {
        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Credenciales del Usuario"));

        formPanel.add(new JLabel("Username:"));
        formPanel.add(txtUser);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(txtPass);
        formPanel.add(new JLabel("Rol:"));
        formPanel.add(comboRole);

        formPanel.add(btnAdd);
        formPanel.add(btnUpdate);
        formPanel.add(btnDelete);
        formPanel.add(btnExit);

        String[] columns = {"Username", "Password", "Rol"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(userTable), BorderLayout.CENTER);
    }

    private void setupListeners() {
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillFieldsFromSelection();
            }
        });

        btnAdd.addActionListener(e -> handleRegistration());
        btnUpdate.addActionListener(e -> handleUpdate());
        btnDelete.addActionListener(e -> handleDelete());
        btnExit.addActionListener(e -> {
            this.dispose();
            new Dashboard(currentUser, controller.getFileManager()).setVisible(true);
        });
    }

    private void handleRegistration() {
        try {
            controller.processUserRegistration(getUsername(), getPassword(), getSelectedRole());
            showInfoMessage("El usuario ha sido registrado con éxito");
            refreshTableData();
            clearFields();
        } catch (Exception ex) {
            showErrorMessage(ex.getMessage());
        }
    }

    private void handleUpdate() {
        try {
            controller.processUserUpdate(getUsername(), getPassword(), getSelectedRole());
            showInfoMessage("El usuario se ha modificado con éxito");
            refreshTableData();
            clearFields();
        } catch (Exception ex) {
            showErrorMessage(ex.getMessage());
        }
    }

    private void handleDelete() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Desea eliminar el usuario de la base de datos?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                controller.processUserDeletion(getUsername());
                showInfoMessage("El usuario ha sido eliminado con éxito.");
                refreshTableData();
                clearFields();
            } catch (Exception ex) {
                showErrorMessage(ex.getMessage());
            }
        }
    }

    private void fillFieldsFromSelection() {
        int row = userTable.getSelectedRow();
        if (row != -1) {
            txtUser.setText(tableModel.getValueAt(row, 0).toString());
            txtPass.setText(tableModel.getValueAt(row, 1).toString());
            comboRole.setSelectedItem(tableModel.getValueAt(row, 2).toString());

            txtUser.setEditable(false);
        }
    }

    public String getUsername() {
        return txtUser.getText().trim();
    }

    public String getPassword() {
        return new String(txtPass.getPassword());
    }

    public String getSelectedRole() {
        return (String) comboRole.getSelectedItem();
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

    private void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public void refreshTableData() {
        DefaultTableModel model = (DefaultTableModel) userTable.getModel();
        model.setRowCount(0);

        try {
            List<String[]> user = controller.getAllUsers();

            for (String[] users : user) {
                model.addRow(users);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
    }
}
