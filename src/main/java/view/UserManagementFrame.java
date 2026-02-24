package view;

import controller.UserController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.entities.User;
import java.util.List;
import javax.swing.border.TitledBorder;

public class UserManagementFrame extends BaseFrame {

    private final JTextField txtUser;
    private final JPasswordField txtPass;
    private final JTextField txtFullName;
    private final JComboBox<String> comboRole;
    private final JComboBox<String> comboSede;

    private final JButton btnAdd;
    private final JButton btnUpdate;
    private final JButton btnDelete;
    private final JButton btnBack;

    private JTable userTable;
    private DefaultTableModel tableModel;

    private final User currentUser;
    private final UserController controller;

    private JButton btnShowPass;
    private boolean isPassVisible = false;

    public UserManagementFrame(User user, UserController controller) {
        super("HEAP HAVEN - ADMINISTRACIÓN DE SEGURIDAD", 850, 650);
        this.currentUser = user;
        this.controller = controller;

        // 1. Inicializar Componentes con Estilo
        txtUser = createStyledTextField();
        txtPass = createStyledPasswordField();
        txtFullName = createStyledTextField();

        comboRole = new JComboBox<>(new String[]{"ADMIN", "OPERATOR"});
        comboSede = new JComboBox<>();
        styleComboBox(comboRole);
        styleComboBox(comboSede);

        btnAdd = createStyledButton("Registrar", true);
        btnUpdate = createStyledButton("Modificar", false);
        btnDelete = createStyledButton("Eliminar", false);
        btnBack = createStyledButton("Volver", false);

        // 2. Configuración de Layout
        getContentPane().setLayout(new BorderLayout());
        this.setupCustomTitleBar("CONTROL DE ACCESO Y USUARIOS - HEAP HAVEN");

        setupComponents();
        setupListeners();
        refreshTableData();
        fillParkingCombo();

        try {
            controller.loadInitialUsers();
        } catch (Exception e) {
            showErrorMessage("Error al cargar usuarios: " + e.getMessage());
        }

        setVisible(true);
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

    private void setupComponents() {
        JPanel mainContent = new JPanel(new BorderLayout(15, 15));
        mainContent.setOpaque(false);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_CELESTE, 1), " CREDENCIALES DE ACCESO ");
        border.setTitleColor(COLOR_CELESTE);
        border.setTitleFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("NOMBRE COMPLETO:"), gbc);
        gbc.gridy = 1;
        formPanel.add(txtFullName, gbc);
        gbc.gridy = 2;
        formPanel.add(createLabel("USUARIO:"), gbc);
        gbc.gridy = 3;
        formPanel.add(txtUser, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(createLabel("CONTRASEÑA:"), gbc);
        JPanel passWrapper = new JPanel(new BorderLayout(5, 0));
        passWrapper.setOpaque(false);
        btnShowPass = new JButton("👁");
        styleViewPassButton(btnShowPass);
        passWrapper.add(txtPass, BorderLayout.CENTER);
        passWrapper.add(btnShowPass, BorderLayout.EAST);
        gbc.gridy = 1;
        formPanel.add(passWrapper, gbc);
        gbc.gridy = 2;
        formPanel.add(createLabel("ROL / SEDE ASIGNADA:"), gbc);
        JPanel comboPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        comboPanel.setOpaque(false);
        comboPanel.add(comboRole);
        comboPanel.add(comboSede);
        gbc.gridy = 3;
        formPanel.add(comboPanel, gbc);
        JPanel actionContainer = new JPanel(new BorderLayout());
        actionContainer.setOpaque(false);
        actionContainer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(btnBack);
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(btnAdd);
        rightPanel.add(btnUpdate);
        rightPanel.add(btnDelete);
        actionContainer.add(leftPanel, BorderLayout.WEST);
        actionContainer.add(rightPanel, BorderLayout.EAST);
        JPanel northWrapper = new JPanel(new BorderLayout());
        northWrapper.setOpaque(false);
        northWrapper.add(formPanel, BorderLayout.CENTER);
        northWrapper.add(actionContainer, BorderLayout.SOUTH);
        String[] columns = {"USUARIO", "NOMBRE", "CONTRASEÑA", "ROL", "SEDE ASIGNADA"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        applyCustomTableStyle(userTable);
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.getViewport().setBackground(COLOR_FONDO);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60, 60, 60)),
                BorderFactory.createEmptyBorder(10, 0, 0, 0)
        ));
        JLabel lblTableTitle = createLabel("REGISTRO DE USUARIOS DEL SISTEMA");
        lblTableTitle.setForeground(COLOR_CELESTE);
        lblTableTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.add(lblTableTitle, BorderLayout.NORTH);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        mainContent.add(northWrapper, BorderLayout.NORTH);
        mainContent.add(tableContainer, BorderLayout.CENTER);
        getContentPane().add(mainContent, BorderLayout.CENTER);
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

        btnShowPass.addActionListener(e -> {
            isPassVisible = !isPassVisible;
            if (isPassVisible) {
                txtPass.setEchoChar((char) 0);
                btnShowPass.setText("🔒");
                btnShowPass.setForeground(COLOR_CELESTE);
            } else {
                txtPass.setEchoChar('•');
                btnShowPass.setText("👁");
                btnShowPass.setForeground(Color.GRAY);
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
                    tableModel.addRow(new Object[]{
                        row[0],
                        row[3],
                        "••••••••",
                        row[2],
                        row[4]
                    });
                }
            }
        } catch (Exception e) {
            showErrorMessage("Error al cargar tabla: " + e.getMessage());
        }
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(180, 180, 180));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        return lbl;
    }

    private JTextField createStyledTextField() {
        JTextField tf = new JTextField();
        tf.setBackground(COLOR_ACCENTO);
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(COLOR_CELESTE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return tf;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setBackground(COLOR_ACCENTO);
        pf.setForeground(Color.WHITE);
        pf.setCaretColor(COLOR_CELESTE);
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 70)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return pf;
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setBackground(COLOR_ACCENTO);
        cb.setForeground(Color.WHITE);
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ((JLabel) cb.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void styleViewPassButton(JButton btn) {
        btn.setPreferredSize(new Dimension(40, 0));
        btn.setBackground(COLOR_ACCENTO);
        btn.setForeground(Color.GRAY);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(60, 60, 60));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(COLOR_ACCENTO);
            }
        });
    }
}
