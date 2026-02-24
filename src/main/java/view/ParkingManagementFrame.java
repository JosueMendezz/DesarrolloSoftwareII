package view;

import controller.ParkingController;
import model.entities.ParkingLot;
import model.entities.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableCellRenderer;

public class ParkingManagementFrame extends BaseFrame {

    private JTable tblParkings;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnUpdate, btnDelete, btnVisualize, btnBack;
    private final User currentUser;
    private final ParkingController controller;

    public ParkingManagementFrame(User user, ParkingController controller) {
        super("HEAP HAVEN - ADMINISTRACIÓN DE PARQUEOS", 900, 600);
        this.currentUser = user;
        this.controller = controller;
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);
        this.setupCustomTitleBar("GESTIÓN DE SEDES OPERATIVAS");
        setupComponents();
        setupListeners();
        refreshTableData();
        setVisible(true);
    }

    private void setupComponents() {
        JPanel mainContent = new JPanel(new BorderLayout(15, 15));
        mainContent.setOpaque(false);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel lblTitle = new JLabel("LISTADO DE INFRAESTRUCTURAS");
        lblTitle.setForeground(COLOR_CELESTE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        mainContent.add(lblTitle, BorderLayout.NORTH);
        String[] columns = {"ID", "Sede", "Capacidad Total", "Cupos Preferenciales"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblParkings = new JTable(tableModel);
        applyDarkTableStyle(tblParkings);
        JScrollPane scrollPane = new JScrollPane(tblParkings);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        scrollPane.getViewport().setBackground(COLOR_FONDO);
        mainContent.add(scrollPane, BorderLayout.CENTER);
        JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelActions.setOpaque(false);
        btnAdd = new JButton("Crear Sede");
        btnUpdate = new JButton("Modificar");
        btnDelete = new JButton("Eliminar");
        btnVisualize = new JButton("Visualizar Estructura");
        btnBack = new JButton("Volver");
        styleButton(btnAdd, false);
        styleButton(btnUpdate, false);
        styleButton(btnDelete, false);
        styleButton(btnVisualize, true);
        styleButton(btnBack, false);
        panelActions.add(btnAdd);
        panelActions.add(btnUpdate);
        panelActions.add(btnDelete);
        panelActions.add(Box.createHorizontalStrut(20));
        panelActions.add(btnVisualize);
        panelActions.add(btnBack);
        mainContent.add(panelActions, BorderLayout.SOUTH);
        getContentPane().add(mainContent, BorderLayout.CENTER);
    }

    private void setupListeners() {
        btnAdd.addActionListener(e -> {
            new ParkingCreateFrame(currentUser, controller).setVisible(true);
            this.dispose();
        });

        btnUpdate.addActionListener(e -> handleUpdate());
        btnDelete.addActionListener(e -> handleDelete());
        btnVisualize.addActionListener(e -> handleVisualize());

        btnBack.addActionListener(e -> {
            if (tblParkings.getSelectedRow() != -1) {
                tblParkings.clearSelection();
            } else {
                this.dispose();
                new Dashboard(currentUser, controller.getFileManager()).setVisible(true);
            }
        });
    }

    private void handleVisualize() {
        int selectedRow = tblParkings.getSelectedRow();
        if (selectedRow != -1) {
            try {
                String name = tableModel.getValueAt(selectedRow, 1).toString();
                List<String[]> summary = controller.getFilteredParkingSummary(currentUser);
                new ParkingConfigView(this, summary, controller, name).setVisible(true);
            } catch (Exception ex) {
                showError("Error al abrir el visor: " + ex.getMessage());
            }
        } else {
            showWarning("Seleccione una sede de la tabla para visualizar su mapa de espacios.");
        }
    }

    private void handleUpdate() {
        int selectedRow = tblParkings.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < tableModel.getRowCount()) {
            try {
                String name = tableModel.getValueAt(selectedRow, 1).toString();
                int currentTotal = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());
                int currentPref = Integer.parseInt(tableModel.getValueAt(selectedRow, 3).toString());
                int occupiedSpaces = controller.getOccupancyCount(name);
                if (occupiedSpaces > 0) {
                    JOptionPane.showMessageDialog(this,
                            "<html>La sede <b>" + name + "</b> tiene vehículos activos.<br>"
                            + "No podrá reducir la capacidad por debajo de " + occupiedSpaces + ".</html>",
                            "Aviso de Ocupación", JOptionPane.INFORMATION_MESSAGE);
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
            String mensaje = "<html>¿Desea eliminar la sede: <b style='color:yellow;'>" + name + "</b>?<br>"
                    + "<font color='red'>Se perderán todas las configuraciones de espacios.</font></html>";

            int confirm = JOptionPane.showConfirmDialog(this, mensaje, "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    controller.deleteParkingBranch(name);
                    refreshTableData();
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            }
        } else {
            showWarning("Debe seleccionar un parqueo.");
        }
    }

    public void refreshTableData() {
        tableModel.setRowCount(0);
        try {
            List<String[]> data = controller.loadAllParkings();
            int idCounter = 1;
            for (String[] row : data) {
                if (row != null && row.length >= 3) {
                    tableModel.addRow(new Object[]{idCounter++, row[0], row[1], row[2]});
                }
            }
        } catch (Exception e) {
            showError("Error al cargar la tabla: " + e.getMessage());
        }
    }

    private void applyDarkTableStyle(JTable t) {
        t.setBackground(COLOR_ACCENTO);
        t.setForeground(Color.WHITE);
        t.setGridColor(new Color(50, 50, 50));
        t.setRowHeight(40);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setSelectionBackground(COLOR_CELESTE);
        t.setSelectionForeground(COLOR_FONDO);
        t.setShowVerticalLines(false);
        t.getTableHeader().setBackground(new Color(30, 30, 30));
        t.getTableHeader().setForeground(COLOR_CELESTE);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.getTableHeader().setPreferredSize(new Dimension(0, 40));
        t.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        t.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        t.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        t.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
    }

    private void styleButton(JButton btn, boolean primary) {
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        if (primary) {
            btn.setBackground(COLOR_CELESTE);
            btn.setForeground(COLOR_FONDO);
        } else {
            btn.setBackground(new Color(60, 60, 60));
            btn.setForeground(Color.WHITE);
        }
        btn.setBorder(BorderFactory.createLineBorder(btn.getBackground().darker()));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Sistema de Seguridad", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Atención Requerida", JOptionPane.WARNING_MESSAGE);
    }
}
