package view;

import controller.VehicleController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

public class RateManagementView extends BaseFrame {

    private JTable tblRates;
    private DefaultTableModel model;
    private final VehicleController controller;
    private List<String[]> originalRates;
    private final Runnable onUpdateCallback;

    public RateManagementView(Frame parent, VehicleController controller, Runnable onUpdateCallback) {
        super("Heap Haven - Gestión de Tarifas", 600, 520);
        this.controller = controller;
        this.onUpdateCallback = onUpdateCallback;

        // Configurar barra personalizada de BaseFrame
        this.setupCustomTitleBar("GESTIÓN DE TARIFAS HORARIAS");

        setupContent();
        loadData();
        this.setLocationRelativeTo(parent);
    }

    private void setupContent() {
        JPanel mainContainer = new JPanel(new BorderLayout(20, 20));
        mainContainer.setOpaque(false);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Cabecera informativa
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("CONFIGURACIÓN DE PRECIOS", JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(COLOR_CELESTE);

        JLabel lblHint = new JLabel("Haga doble clic en el monto para editar la tarifa por hora.");
        lblHint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblHint.setForeground(Color.GRAY);

        pnlHeader.add(lblTitle);
        pnlHeader.add(lblHint);
        mainContainer.add(pnlHeader, BorderLayout.NORTH);

        // Tabla con estilo oscuro "Monitor"
        model = new DefaultTableModel(new String[]{"TIPO DE VEHÍCULO", "TARIFA (₡)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1;
            }
        };

        tblRates = new JTable(model);
        applyDarkTableStyle(tblRates);
        tblRates.getColumnModel().getColumn(1).setCellEditor(new SpinnerEditor());

        JScrollPane scroll = new JScrollPane(tblRates);
        scroll.getViewport().setBackground(COLOR_FONDO);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        mainContainer.add(scroll, BorderLayout.CENTER);

        // Panel de Acciones (Botón Inteligente)
        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlSouth.setOpaque(false);

        JButton btnBack = new JButton("Volver");
        JButton btnSave = new JButton("Actualizar Tarifas");

        styleButton(btnBack, false);
        styleButton(btnSave, true);

        btnBack.addActionListener(e -> handleBackAction());
        btnSave.addActionListener(e -> saveChanges());

        pnlSouth.add(btnBack);
        pnlSouth.add(btnSave);
        mainContainer.add(pnlSouth, BorderLayout.SOUTH);

        this.add(mainContainer, BorderLayout.CENTER);
    }

    private void handleBackAction() {
        if (tblRates.isEditing() || tblRates.getSelectedRow() != -1) {
            if (tblRates.isEditing()) {
                tblRates.getCellEditor().cancelCellEditing();
            }
            tblRates.clearSelection();
            loadData();
        } else {
            this.dispose();
        }
    }

    private void loadData() {
        model.setRowCount(0);
        originalRates = controller.getAllRates();
        for (String[] row : originalRates) {
            model.addRow(new Object[]{row[0], Integer.parseInt(row[1])});
        }
    }

    private void saveChanges() {
        if (tblRates.isEditing()) {
            tblRates.getCellEditor().stopCellEditing();
        }

        List<String> updatedLines = new ArrayList<>();
        boolean huboCambios = false;
        StringBuilder log = new StringBuilder("Resumen de cambios:\n\n");

        for (int i = 0; i < model.getRowCount(); i++) {
            String vehiculo = model.getValueAt(i, 0).toString();
            String nuevoPrecio = model.getValueAt(i, 1).toString();
            String precioOriginal = originalRates.get(i)[1];

            if (!nuevoPrecio.equals(precioOriginal)) {
                huboCambios = true;
                log.append(String.format("• %s: ₡%s ➔ ₡%s\n", vehiculo, precioOriginal, nuevoPrecio));
            }
            updatedLines.add(vehiculo + "|" + nuevoPrecio);
        }

        if (!huboCambios) {
            JOptionPane.showMessageDialog(this, "No se detectaron modificaciones.");
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(this, log.append("\n¿Confirmar cambios?").toString(),
                "Actualizar Sistema", JOptionPane.YES_NO_OPTION);

        if (confirmar == JOptionPane.YES_OPTION) {
            try {
                controller.updateAllRates(updatedLines);
                JOptionPane.showMessageDialog(this, "Tarifas actualizadas correctamente.");

                if (onUpdateCallback != null) {
                    onUpdateCallback.run();
                }

                this.dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error de archivo: " + ex.getMessage());
            }
        }
    }

    private void applyDarkTableStyle(JTable t) {
        t.setBackground(COLOR_ACCENTO);
        t.setForeground(Color.WHITE);
        t.setGridColor(new Color(50, 50, 50));
        t.setRowHeight(45);
        t.setSelectionBackground(COLOR_CELESTE);
        t.setSelectionForeground(COLOR_FONDO);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        t.getTableHeader().setBackground(new Color(35, 35, 35));
        t.getTableHeader().setForeground(COLOR_CELESTE);
        t.getTableHeader().setPreferredSize(new Dimension(0, 35));
    }

    private void styleButton(JButton btn, boolean primary) {
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        if (primary) {
            btn.setBackground(COLOR_CELESTE);
            btn.setForeground(COLOR_FONDO);
        } else {
            btn.setBackground(new Color(60, 60, 60));
            btn.setForeground(Color.WHITE);
        }
        btn.setBorder(BorderFactory.createLineBorder(btn.getBackground().darker()));
    }

    // Editor de celda usando Spinner
    class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {

        private final JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 50));

        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            spinner.setValue(v);
            return spinner;
        }

        public Object getCellEditorValue() {
            return spinner.getValue();
        }
    }
}
