package view;

import controller.ParkingController;
import model.entities.ParkingLot;
import model.entities.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * View for managing multiple parking lot branches.
 */
public class ParkingManagementFrame extends JFrame {

    private JTable tblParkings;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnUpdate, btnDelete, btnBack;

    private final User currentUser;
    private final ParkingController controller;

    public ParkingManagementFrame(User user, ParkingController controller) {
        this.currentUser = user;
        this.controller = controller;

        setupConfiguration();
        setupComponents();
        setupListeners();
        refreshTableData();
    }

    private void setupConfiguration() {
        setTitle("J-Node - Parking Management");
        setSize(700, 450);
        // DISPOSE_ON_CLOSE está bien aquí porque el Dashboard controla el proceso principal
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
    }

    private void setupComponents() {
        JLabel lblTitle = new JLabel("Gestión de Parqueos", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"#", "Nombre del Parqueo", "Capacidad", "Espacios Pref."};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblParkings = new JTable(tableModel);
        tblParkings.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblParkings.getTableHeader().setReorderingAllowed(false); 
        add(new JScrollPane(tblParkings), BorderLayout.CENTER);

        JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnAdd = new JButton("Crear");
        btnUpdate = new JButton("Modificar");
        btnDelete = new JButton("Eliminar");
        btnBack = new JButton("Menú Principal");

        panelActions.add(btnAdd);
        panelActions.add(btnUpdate);
        panelActions.add(btnDelete);
        panelActions.add(btnBack);
        add(panelActions, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        btnAdd.addActionListener(e -> {
            new ParkingCreateFrame(currentUser, controller).setVisible(true);
            this.dispose();
        });

        btnUpdate.addActionListener(e -> handleUpdate());

        btnDelete.addActionListener(e -> handleDelete());

        btnBack.addActionListener(e -> {
            this.dispose(); 
            // Usamos el constructor que inyecta el FileManager
            new Dashboard(currentUser, controller.getFileManager()).setVisible(true);
        });
    }

    private void handleUpdate() {
        int selectedRow = tblParkings.getSelectedRow();
        if (selectedRow != -1) {
            try {
                String name = tableModel.getValueAt(selectedRow, 1).toString();
                int total = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());
                int pref = Integer.parseInt(tableModel.getValueAt(selectedRow, 3).toString());

                // Crear objeto temporal para edición
                ParkingLot parking = new ParkingLot(0, name, total, pref);

                new ParkingCreateFrame(currentUser, controller, parking).setVisible(true);
                this.dispose();

            } catch (NumberFormatException nfe) {
                showError("Invalid data format in table.");
            } catch (Exception ex) {
                showError("Error loading branch data: " + ex.getMessage());
            }
        } else {
            showWarning("Seleccione el parqueo que va a modificar.");
        }
    }

    private void handleDelete() {
        int selectedRow = tblParkings.getSelectedRow();
        if (selectedRow != -1) {
            String name = tableModel.getValueAt(selectedRow, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Desea eliminar el parqueo" + name + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    controller.deleteParkingBranch(name);
                    refreshTableData();
                    JOptionPane.showMessageDialog(this, "Prqueo eliminado exitosamente.");
                } catch (Exception ex) {
                    showError("Error deleting branch: " + ex.getMessage());
                }
            }
        } else {
            showWarning("Seleccione el parqueo que desea eliminar.");
        }
    }

    public void refreshTableData() {
        tableModel.setRowCount(0); 
        try {
            List<String[]> data = controller.loadAllParkings();
            int idCounter = 1;

            for (String[] row : data) {
                // CAMBIO DE SEGURIDAD: Verificamos que no sea nulo y tenga los 3 datos requeridos
                if (row != null && row.length >= 3) {
                    Object[] tableRow = {
                        idCounter++,
                        row[0], // Nombre
                        row[1], // Total
                        row[2]  // Preferencial
                    };
                    tableModel.addRow(tableRow);
                }
            }
        } catch (Exception e) {
            showError("Critical error loading table: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "System Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
    }
}