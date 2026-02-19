package view;

import controller.UserController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.entities.User;
import java.util.List;


public class UserManagementFrame extends JFrame {

    private final JTextField txtUser = new JTextField(15);
    private final JPasswordField txtPass = new JPasswordField(15);
    private final JComboBox<String> comboRole = new JComboBox<>(new String[]{"ADMIN", "OPERATOR"});

    private final JButton btnAdd = new JButton("Registrar");
    private final JButton btnUpdate = new JButton("Modificar");
    private final JButton btnDelete = new JButton("Eliminar");
    private final JButton btnBack = new JButton("Volver");

    private JTable userTable;
    private DefaultTableModel tableModel;

    private final User currentUser;
    private final UserController controller;

    private final JTextField txtFullName = new JTextField(15);
    private final JComboBox<String> comboSede = new JComboBox<>();

    public UserManagementFrame(User user, UserController controller) {
        this.currentUser = user;
        this.controller = controller;

        setupConfiguration();
        setupComponents();
        setupListeners();
        refreshTableData();
        fillParkingCombo();

        try {
            controller.loadInitialUsers();
        } catch (Exception e) {
            showErrorMessage("Error loading users: " + e.getMessage());
        }
    }

    private void fillParkingCombo() {
        comboSede.removeAllItems();
        comboSede.addItem("TODOS");
        try {
            List<String> lines = controller.getFileManager().readAllParkingLines();
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length > 0) {
                        comboSede.addItem(parts[0]);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar sedes: " + e.getMessage());
        }
    }

    private void setupConfiguration() {
        setTitle("Administracion de usuarios - J-Node System");
        setSize(700, 500);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void setupComponents(){
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos del Usuario"));

        formPanel.add(new JLabel("Nombre Completo:"));
        formPanel.add(txtFullName);
        formPanel.add(new JLabel("Usuario:"));
        formPanel.add(txtUser);
        formPanel.add(new JLabel("Contraseña:"));
        formPanel.add(txtPass);
        formPanel.add(new JLabel("Rol:"));
        formPanel.add(comboRole);
        formPanel.add(new JLabel("Asignar Sede:"));
        formPanel.add(comboSede);

        JPanel actionContainer = new JPanel(new BorderLayout());
        actionContainer.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.add(btnBack);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.add(btnAdd);
        rightPanel.add(btnUpdate);
        rightPanel.add(btnDelete);

        actionContainer.add(leftPanel, BorderLayout.WEST);
        actionContainer.add(rightPanel, BorderLayout.EAST);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(formPanel, BorderLayout.CENTER);
        northPanel.add(actionContainer, BorderLayout.SOUTH);

        String[] columns = {"Usuario", "Nombre", "Rol", "Sede Asignada"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);

        add(northPanel, BorderLayout.NORTH);
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

        btnBack.addActionListener(e -> {

            boolean isEditing = !txtUser.isEditable();
            boolean hasText = !txtUser.getText().trim().isEmpty() || !txtFullName.getText().trim().isEmpty();

            if (isEditing || hasText) {
                clearFields();
            } else {
                this.dispose();
                new Dashboard(currentUser, controller.getFileManager()).setVisible(true);
            }
        });
    }

    private void handleRegistration() {
        try {
            String username = getUsername();
            String pass = getPassword();
            String name = txtFullName.getText().trim();
            String sede = (String) comboSede.getSelectedItem();

            if (username.length() < 4) {
                throw new Exception("El usuario debe tener al menos 4 caracteres.");
            }
            if (pass.length() < 4) {
                throw new Exception("La contraseña es muy corta.");
            }
            if (name.isEmpty()) {
                throw new Exception("El nombre es obligatorio.");
            }
            if (sede == null || sede.equals("TODOS") && getSelectedRole().equals("OPERATOR")) {
                throw new Exception("Un OPERADOR debe tener una sede específica asignada.");
            }

            controller.processUserRegistration(username, pass, getSelectedRole(), name, sede);

            showInfoMessage("Usuario '" + username + "' registrado correctamente.");
            refreshTableData();
            clearFields();
        } catch (Exception ex) {
            showErrorMessage(ex.getMessage());
        }
    }

    private void handleUpdate() {
        try {
            String name = txtFullName.getText().trim();
            String sede = (String) comboSede.getSelectedItem();
            String role = (String) comboRole.getSelectedItem();

            controller.processUserUpdate(
                    getUsername(),
                    getPassword(),
                    role,
                    name,
                    sede
            );

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
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

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
            String username = tableModel.getValueAt(row, 0).toString();

            User u = controller.getCachedUsers().stream()
                    .filter(user -> user.getUsername().equals(username))
                    .findFirst().orElse(null);

            if (u != null) {
                txtUser.setText(u.getUsername());
                txtPass.setText(u.getPassword());
                txtFullName.setText(u.getFullName());
                comboRole.setSelectedItem(u.getRole());
                comboSede.setSelectedItem(u.getAssignedParking());

                txtUser.setEditable(false);
            }
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
        txtFullName.setText("");
        comboRole.setSelectedIndex(0);
        comboSede.setSelectedIndex(0);

        txtUser.setEditable(true);
        userTable.clearSelection();
    }

    private void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Informacion", JOptionPane.INFORMATION_MESSAGE);
    }

    public void refreshTableData() {
        tableModel.setRowCount(0);
        try {
            List<String[]> users = controller.getAllUsers();
            for (String[] row : users) {
                if (row.length >= 5) {
                    tableModel.addRow(new Object[]{row[0], row[3], row[2], row[4]});
                }
            }
        } catch (Exception e) {
            showErrorMessage("Error al cargar tabla: " + e.getMessage());
        }
    }
}
