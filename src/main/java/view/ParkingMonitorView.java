package view;

import controller.VehicleController;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Vista para monitorear el estado actual de los parqueos.
 */
public class ParkingMonitorView extends JPanel {

    private final VehicleController vehicleController;
    private JTextField txtSearchSpace;
    private JTable tblMonitor;
    private JComboBox<String> cbParkings;
    private DefaultTableModel tableModel;

    public ParkingMonitorView(VehicleController vehicleController) {
        this.vehicleController = vehicleController;
        initComponents();
        loadParkingNames();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout());

        JPanel pnlNorth = new JPanel(new FlowLayout(FlowLayout.LEFT));

        pnlNorth.add(new JLabel("Parqueo:"));
        cbParkings = new JComboBox<>();
        cbParkings.addActionListener(e -> refreshTableData());
        pnlNorth.add(cbParkings);

        pnlNorth.add(new JLabel("Buscar Espacio:"));
        txtSearchSpace = new JTextField(8);
        pnlNorth.add(txtSearchSpace);

        JButton btnSearch = new JButton("Filtrar");
        btnSearch.addActionListener(e -> refreshTableData());
        pnlNorth.add(btnSearch);

        JButton btnCheckout = new JButton("Retirar y Cobrar");
        btnCheckout.setBackground(new Color(220, 53, 69));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.addActionListener(e -> handleCheckout());
        pnlNorth.add(btnCheckout);

        String[] columns = {"Espacio", "Placa", "Dueño", "Modelo", "Marca", "Tipo", "Tarifa/h", "Hora Entrada"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblMonitor = new JTable(tableModel);

        this.add(new JScrollPane(tblMonitor), BorderLayout.CENTER);
        this.add(pnlNorth, BorderLayout.NORTH);
    }

    private void loadParkingNames() {
        try {
            cbParkings.removeAllItems();
            List<String> names = vehicleController.getAvailableParkingNames();
            for (String name : names) {
                cbParkings.addItem(name);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar nombres: " + e.getMessage(),
                    "Error de Carga", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTableData() {
        String selected = (String) cbParkings.getSelectedItem();
        if (selected == null) {
            return;
        }

        try {
            tableModel.setRowCount(0);
            String spaceCriteria = txtSearchSpace.getText().trim();
            List<Object[]> rows = vehicleController.getParkedVehiclesStatus(selected, spaceCriteria);
            for (Object[] row : rows) {
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
    }

    private void handleCheckout() {

        int selectedRow = tblMonitor.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un vehículo de la tabla.");
            return;
        }

        String space = tblMonitor.getValueAt(selectedRow, 0).toString();
        String plate = tblMonitor.getValueAt(selectedRow, 1).toString();
        String owner = tblMonitor.getValueAt(selectedRow, 2).toString();
        String type = tblMonitor.getValueAt(selectedRow, 5).toString();
        String rateStr = tblMonitor.getValueAt(selectedRow, 6).toString().replaceAll("[^0-9.]", "");
        String entryTime = tblMonitor.getValueAt(selectedRow, 7).toString();

        double hourlyRate = Double.parseDouble(rateStr);
        double total = vehicleController.calculateAmount(entryTime, hourlyRate);
        String exitTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // Construcción del mensaje factura
        String message = String.format(
                "--- CREDENCIALES ---\n"
                + "Espacio: %s | Placa: %s\n"
                + "Dueño: %s\n\n"
                + "--- DETALLES DE COBRO ---\n"
                + "Tarifa/h: ₡%s\n"
                + "Entrada: %s\n"
                + "Salida: %s\n"
                + "--------------------------\n"
                + "TOTAL A PAGAR: ₡%.2f\n",
                space, plate, owner, rateStr, entryTime, exitTime, total
        );

        Object[] options = {"Cobrar", "Cancelar"};
        int choice = JOptionPane.showOptionDialog(this, message, "Resumen de Salida",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            try {
                vehicleController.processPayment(plate);
                refreshTableData();
                JOptionPane.showMessageDialog(this, "Vehículo retirado y pago procesado.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al procesar: " + e.getMessage());
            }
        }
    }
}
