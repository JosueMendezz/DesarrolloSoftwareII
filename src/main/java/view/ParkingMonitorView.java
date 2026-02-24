package view;

import controller.VehicleController;
import model.entities.User;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ParkingMonitorView extends JPanel {

    private final Color COLOR_FONDO = new Color(20, 20, 20);
    private final Color COLOR_ACCENTO = new Color(35, 35, 35);
    private final Color COLOR_CELESTE = new Color(0, 191, 255);
    private final Color COLOR_GRIS_MATE = new Color(45, 45, 45);

    private final VehicleController vehicleController;
    private final User currentUser;
    private JTextField txtSearchSpace;
    private JTable tblMonitor;
    private JComboBox<String> cbParkings;
    private DefaultTableModel tableModel;
    private JButton btnSearch, btnCheckout, btnDetails, btnRefresh;

    public ParkingMonitorView(VehicleController vehicleController, User user) {
        this.vehicleController = vehicleController;
        this.currentUser = user;
        initComponents();
        loadParkingNames();
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBackground(COLOR_FONDO);
        this.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- PANEL NORTE: CONTROL OPERATIVO ---
        JPanel pnlNorth = new JPanel(new BorderLayout());
        pnlNorth.setOpaque(false);

        // Grupo Filtros con etiquetas estilizadas
        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlLeft.setOpaque(false);

        cbParkings = new JComboBox<>();
        styleComboBox(cbParkings);

        txtSearchSpace = new JTextField(8);
        styleTextField(txtSearchSpace);

        btnSearch = new JButton("Filtrar");
        styleButton(btnSearch, false);

        btnRefresh = new JButton("<>");
        styleButton(btnRefresh, false);

        pnlLeft.add(createLabel("SEDE:"));
        pnlLeft.add(cbParkings);
        pnlLeft.add(createLabel("ESPACIO:"));
        pnlLeft.add(txtSearchSpace);
        pnlLeft.add(btnSearch);
        pnlLeft.add(btnRefresh);

        // Grupo Acciones
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        pnlRight.setOpaque(false);

        btnDetails = new JButton("Ver Detalles");
        styleButton(btnDetails, false);

        btnCheckout = new JButton("Retirar y Cobrar");
        styleButton(btnCheckout, true);

        pnlRight.add(btnDetails);
        pnlRight.add(btnCheckout);

        pnlNorth.add(pnlLeft, BorderLayout.WEST);
        pnlNorth.add(pnlRight, BorderLayout.EAST);

        // --- PANEL CENTRAL: TABLA ---
        setupTable();

        this.add(pnlNorth, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(tblMonitor);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        scrollPane.getViewport().setBackground(COLOR_FONDO);
        this.add(scrollPane, BorderLayout.CENTER);

        setupListeners();
    }

    private void setupListeners() {

        btnSearch.addActionListener(e -> {
            String texto = txtSearchSpace.getText().trim();
            if (texto.isEmpty() || texto.equals("Espacio...")) {
                JOptionPane.showMessageDialog(this,
                        "Debes ingresar un número de espacio para filtrar el vehículo.",
                        "Atención",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                refreshTableData();
            }
        });
        btnRefresh.addActionListener(e -> handleResetView());

        // REQUERIMIENTO C: Botón de Detalles
        btnDetails.addActionListener(e -> handleViewDetails());

        // REQUERIMIENTO B: Lógica de Cobro y History
        btnCheckout.addActionListener(e -> handleCheckout());

        cbParkings.addActionListener(e -> refreshTableData());
    }

    private void handleViewDetails() {
        int row = tblMonitor.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un vehículo de la tabla.");
            return;
        }

        String placa = tableModel.getValueAt(row, 1).toString();

        // 1. Obtenemos el HTML del controlador
        String htmlContent = vehicleController.getFullVehicleInfo(placa);

        // 2. Crear el Diálogo personalizado
        JDialog detailsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Expediente: " + placa, true);
        detailsDialog.setSize(450, 600);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.getContentPane().setBackground(new Color(30, 30, 30));

        // 3. Usar un JEditorPane para renderizar el HTML
        JEditorPane infoPane = new JEditorPane();
        infoPane.setContentType("text/html");
        infoPane.setText(htmlContent);
        infoPane.setEditable(false);
        infoPane.setOpaque(false);
        infoPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 4. Botón de cierre 
        JButton btnClose = new JButton("Cerrar Expediente");
        btnClose.setBackground(new Color(33, 150, 243));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btnClose.addActionListener(e -> detailsDialog.dispose());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.add(btnClose);

        // Ensamblar
        detailsDialog.add(new JScrollPane(infoPane), BorderLayout.CENTER);
        detailsDialog.add(btnPanel, BorderLayout.SOUTH);

        detailsDialog.setVisible(true);
    }

    private void loadParkingNames() {
        try {
            cbParkings.removeAllItems();

            if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
                List<String> names = vehicleController.getAvailableParkingNames();
                for (String name : names) {
                    cbParkings.addItem(name);
                }
            } else {
                String sedeAsignada = currentUser.getAssignedParking();
                cbParkings.addItem(sedeAsignada);
                cbParkings.setEnabled(false); // Bloqueamos para que no cambie de sede
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar nombres: " + e.getMessage());
        }
    }

    protected void refreshTableData() {
        String selected = (String) cbParkings.getSelectedItem();
        if (selected == null) {
            return;
        }

        try {
            tableModel.setRowCount(0);
            String spaceCriteria = txtSearchSpace.getText().trim();

            List<Object[]> rows = vehicleController.getParkedVehiclesStatus(selected, spaceCriteria);

            for (Object[] raw : rows) {
                Object[] reorderedRow = new Object[7]; // Las 7 columnas de la tabla

                // raw[0] = space | raw[1] = placa | raw[2] = ownerName | raw[3] = modelo 
                // raw[4] = marca | raw[5] = tipo | raw[6] = tarifa | raw[7] = entrada
                reorderedRow[0] = raw[0];  // Espacio
                reorderedRow[1] = raw[1];  // Placa
                reorderedRow[2] = raw[2];  // Dueño
                reorderedRow[3] = raw[5];  // Tipo (Automóvil/Moto)

                // 1. PREFERENCIAL: Como el controlador no lo envía en este método, 
                // lo dejamos en "NO" por defecto 
                reorderedRow[4] = "NO";

                // 2. ENTRADA: Es el índice 7 del controlador
                reorderedRow[5] = raw[7];

                // 3. TARIFA: Es el índice 6 del controlador
                reorderedRow[6] = raw[6];

                tableModel.addRow(reorderedRow);
            }

            if (rows.isEmpty() && !spaceCriteria.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontró ningún vehículo en el espacio: " + spaceCriteria);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de mapeo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleCheckout() {
        int row = tblMonitor.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un vehículo de la tabla para procesar el cobro.");
            return;
        }

        try {
            String placa = tableModel.getValueAt(row, 1).toString();
            String sede = cbParkings.getSelectedItem().toString();

            // Buscamos los datos del vehículo en el archivo a través del controller
            List<String[]> todos = vehicleController.getFileManager().loadAllParkedVehicles();
            String[] v = todos.stream()
                    .filter(veh -> veh[0].equalsIgnoreCase(placa))
                    .findFirst()
                    .orElseThrow(() -> new Exception("Vehículo no encontrado."));

            // v[1] es tipo, v[8] es ownerId, v[11] es hora entrada, v[10] es espacio
            double tarifa = vehicleController.calculateAmount(v[11], 0);
            double monto = vehicleController.calculateFinalPrice(placa, sede);
            String cliente = vehicleController.findCustomerById(v[8]).getName();

            // 2. Pasamos todo al ticket
            showTicketAndProcess(placa, monto, sede, cliente, v[11], v[10], v[1]);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en cobro: " + ex.getMessage());
        }
    }

    private void showTicketAndProcess(String plate, double amount, String branch, String customerName, String entryTimeRaw, String spaceId, String vehicleType) {
        // 1. DATE STANDARDIZATION
        // Input format from file is "yyyy-MM-dd HH:mm"
        // Output format for ticket is "dd/MM/yyyy HH:mm"
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String formattedEntry;
        try {
            LocalDateTime parsedEntry = LocalDateTime.parse(entryTimeRaw, inputFormatter);
            formattedEntry = parsedEntry.format(outputFormatter);
        } catch (Exception e) {
            formattedEntry = entryTimeRaw; // Fallback
        }

        String exitTime = LocalDateTime.now().format(outputFormatter);

        // 2. BUSINESS LOGIC
        double hourlyRate = (amount > 0) ? vehicleController.getRateFromFile(vehicleType) : 0;
        int estimatedHours = (int) Math.ceil(amount / (hourlyRate > 0 ? hourlyRate : 1));

        // 3. TICKET UI
        String ticketHtml = String.format(
                "<html><body style='width: 300px; font-family: sans-serif; padding: 10px;'>"
                + "<div style='text-align:center;'>"
                + "  <h1 style='margin:0; color:#2196F3; font-size: 18pt;'>Heap Haven</h1>"
                + "  <p style='margin:0; font-size: 11pt;'><b>Sede %s</b></p>"
                + "</div>"
                + "<hr style='border-top: 1px dashed #000;'>"
                + "<table style='width:100%%; font-size: 12pt;'>"
                + "  <tr><td><b>Cliente:</b></td><td style='text-align:right;'>%s</td></tr>"
                + "  <tr><td><b>Placa:</b></td><td style='text-align:right;'>%s</td></tr>"
                + "  <tr><td><b>Espacio:</b></td><td style='text-align:right;'>#%s</td></tr>"
                + "</table>"
                + "<hr style='border-top: 1px dashed #000;'>"
                + "<div style='font-size: 11pt;'>"
                + "  <b style='font-size: 12pt;'>DESGLOSE DE COBRO:</b><br>"
                + "  <table style='width:100%%; font-size: 11pt; margin-top: 5px;'>"
                + "    <tr><td>Entrada:</td><td style='text-align:right;'>%s</td></tr>"
                + "    <tr><td>Salida:</td><td style='text-align:right;'>%s</td></tr>"
                + "    <tr><td>Tarifa/Hora:</td><td style='text-align:right;'>₡%.2f</td></tr>"
                + "    <tr><td>Tiempo total:</td><td style='text-align:right;'><b>%d hora(s)</b></td></tr>"
                + "  </table>"
                + "</div>"
                + "<hr style='border-top: 1px dashed #000;'>"
                + "<table style='width:100%%;'>"
                + "  <tr>"
                + "    <td style='font-size: 13pt;'><b>TOTAL:</b></td>"
                + "    <td style='text-align:right; font-size: 20pt; color:#2E7D32;'><b>₡%.2f</b></td>"
                + "  </tr>"
                + "</table>"
                + "<br><div style='text-align:center; font-size: 10pt; color:#444;'>"
                + "  Cajero: %s<br>"
                + "  ¡Gracias por su preferencia!"
                + "</div>"
                + "</body></html>",
                branch, customerName, plate, spaceId,
                formattedEntry, exitTime, hourlyRate, estimatedHours, amount, currentUser.getFullName()
        );

        Object[] options = {"Confirmar Pago", "Cancelar"};
        int response = JOptionPane.showOptionDialog(this, ticketHtml, "Finalizar Operación",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (response == 0) {
            try {
                vehicleController.finalizeTransaction(plate, amount, currentUser.getUsername(), branch);
                refreshTableData();
                JOptionPane.showMessageDialog(this, "Transacción finalizada con éxito.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar en historial: " + ex.getMessage());
            }
        }
    }

    private void handleResetView() {
        // 1. Limpiar el cuadro de texto del espacio
        txtSearchSpace.setText("");

        // 2. Ejecutar el refresco de datos (ahora traerá todo porque el campo está vacío)
        refreshTableData();

        // 3. Quitar cualquier selección de fila de la tabla
        tblMonitor.clearSelection();
    }

    private void setupTable() {
        String[] columns = {"Espacio", "Placa", "Dueño", "Tipo", "Preferencial", "Entrada", "Tarifa"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblMonitor = new JTable(tableModel);
        tblMonitor.setRowHeight(40);
        tblMonitor.setBackground(COLOR_ACCENTO);
        tblMonitor.setForeground(Color.WHITE);
        tblMonitor.setGridColor(new Color(50, 50, 50));
        tblMonitor.setSelectionBackground(COLOR_CELESTE);
        tblMonitor.setSelectionForeground(COLOR_FONDO);
        tblMonitor.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Estilo del encabezado
        tblMonitor.getTableHeader().setBackground(new Color(30, 30, 30));
        tblMonitor.getTableHeader().setForeground(COLOR_CELESTE);
        tblMonitor.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblMonitor.getTableHeader().setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
    }

    private void styleComboBox(JComboBox<?> cb) {
        cb.setBackground(COLOR_GRIS_MATE);
        cb.setForeground(Color.WHITE);
        cb.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        cb.setFocusable(false);

        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                lbl.setBackground(isSelected ? COLOR_CELESTE : COLOR_GRIS_MATE);
                lbl.setForeground(isSelected ? COLOR_FONDO : Color.WHITE);
                lbl.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return lbl;
            }
        });

        cb.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = new JButton("▼");
                btn.setFont(new Font("Arial", Font.PLAIN, 7));
                btn.setBackground(COLOR_GRIS_MATE);
                btn.setForeground(Color.GRAY);
                btn.setBorderPainted(false);
                btn.setContentAreaFilled(false);
                return btn;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(COLOR_GRIS_MATE);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });
    }

    private void styleButton(JButton btn, boolean primary) {
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        if (primary) {
            btn.setBackground(COLOR_CELESTE);
            btn.setForeground(COLOR_FONDO);
        } else {
            btn.setBackground(new Color(60, 60, 60));
            btn.setForeground(Color.WHITE);
        }
    }

    private void styleTextField(JTextField tf) {
        tf.setBackground(COLOR_ACCENTO);
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(COLOR_CELESTE);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 80, 80)),
                BorderFactory.createEmptyBorder(2, 5, 2, 5)));
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(new Color(150, 150, 150));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        return lbl;
    }
}
