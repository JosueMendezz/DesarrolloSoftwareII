package view;

import controller.ParkingController;
import model.entities.ParkingLot;
import model.entities.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

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
        setTitle("J-Node - Administración de parqueos ");
        setSize(700, 450);
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
            new Dashboard(currentUser, controller.getFileManager()).setVisible(true);
        });
    }

    private void handleUpdate() {
        int selectedRow = tblParkings.getSelectedRow();
        if (selectedRow != -1) {
            try {
                String name = tableModel.getValueAt(selectedRow, 1).toString();
                int currentTotal = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());
                int currentPref = Integer.parseInt(tableModel.getValueAt(selectedRow, 3).toString());

                int occupiedSpaces = controller.getOccupancyCount(name);

                if (occupiedSpaces > 0) {
                    JOptionPane.showMessageDialog(this,
                            "<html>Este parqueo tiene <b>" + occupiedSpaces + " vehículos</b> estacionados.<br>"
                            + "Al modificarlo, no podrá definir una capacidad menor a esa cantidad.</html>",
                            "Información de Ocupación", JOptionPane.INFORMATION_MESSAGE);
                }

                ParkingLot parking = new ParkingLot(0, name, currentTotal, currentPref);
                new ParkingCreateFrame(currentUser, controller, parking).setVisible(true);
                this.dispose();

            } catch (Exception ex) {
                showError("Error cargando datos de la sede: " + ex.getMessage());
            }
        } else {
            showWarning("Seleccione el parqueo que va a modificar.");
        }
    }

    private void handleDelete() {
        int selectedRow = tblParkings.getSelectedRow();

        if (selectedRow != -1) {
            String name = tableModel.getValueAt(selectedRow, 1).toString();

            String mensaje = "<html>¿Está seguro de que desea eliminar el parqueo: <b>" + name + "</b>?<br>"
                    + "<font color='red'>Esta acción eliminará todos los archivos de configuración asociados.</font></html>";

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    mensaje,
                    "¡Advertencia de Eliminación!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    controller.deleteParkingBranch(name);

                    refreshTableData();
                    JOptionPane.showMessageDialog(this, "El parqueo '" + name + "' ha sido removido del sistema.",
                            "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "No se pudo eliminar", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un parqueo de la lista para poder eliminarlo.",
                    "Selección Requerida", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refreshTableData() {
        tableModel.setRowCount(0);
        try {
            List<String[]> data = controller.loadAllParkings();
            int idCounter = 1;

            for (String[] row : data) {
                if (row != null && row.length >= 3) {
                    Object[] tableRow = {
                        idCounter++,
                        row[0],
                        row[1],
                        row[2]
                    };
                    tableModel.addRow(tableRow);
                }
            }
        } catch (Exception e) {
            showError("Error critico al cargar la tabla: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error del sistema", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Cuidado", JOptionPane.WARNING_MESSAGE);
    }
}
