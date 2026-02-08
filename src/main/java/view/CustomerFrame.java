package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;

public class CustomerFrame extends JFrame {

    private JTextField txtId = new JTextField(15);
    private JTextField txtName = new JTextField(15);
    private JCheckBox chkPreferential = new JCheckBox("¿Espacio Preferencial? ");
    private JButton registerButton = new JButton("Registrar Cliente");
    private JTable customerTable;
    private DefaultTableModel customerTableModel;

    public CustomerFrame() {
        setTitle("Módulo de Clientes - J-Node System");
        setSize(550, 450);
        setLayout(new BorderLayout(10, 10));

        // Formulario (Arriba)
        JPanel panelForm = new JPanel(new GridLayout(4, 1, 5, 5));
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));

        panelForm.add(new JLabel("Cédula / ID:"));
        panelForm.add(txtId);
        panelForm.add(new JLabel("Nombre Completo:"));
        panelForm.add(txtName);
        panelForm.add(chkPreferential);
        panelForm.add(registerButton);

        add(panelForm, BorderLayout.NORTH);

        // Tabla (Centro)
        customerTableModel = new DefaultTableModel(new Object[]{"ID", "Nombre", "Preferencial"}, 0);
        customerTable = new JTable(customerTableModel);
        add(new JScrollPane(customerTable), BorderLayout.CENTER);

        setLocationRelativeTo(null);
    }

    // Getters para el Controller
    public String getId() {
        return txtId.getText();
    }

    public String getName() {
        return txtName.getText();
    }

    public boolean isPreferential() {
        return chkPreferential.isSelected();
    }

    public DefaultTableModel getTableModel() {
        return customerTableModel;
    }

    public void addSaveListener(ActionListener listener) {
        registerButton.addActionListener(listener);
    }

    public void clearFields() {
        txtId.setText("");
        txtName.setText("");
        chkPreferential.setSelected(false);
    }
}
