/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;
import model.data.FileManager;
import model.entities.User;
import java.util.List;

/**
 *
 * @author Caleb Murillo
 */
public class ParkingManagementFrame extends JFrame {

    private JTable tblParkings;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnUpdate, btnDelete, btnBack;

    private User currentUser;

    public ParkingManagementFrame(User user) {
        this.currentUser = user;
        setTitle("J-Node - Gestión de Parqueos");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        // Título
        JLabel lblTitle = new JLabel("Administración de Sedes de Parqueo", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        // Tabla de Parqueos
        String[] columns = {"ID", "Nombre", "Capacidad Total", "Espacios Pref."};

        tableModel = new DefaultTableModel(columns, 0);
        tblParkings = new JTable(tableModel);
        add(new JScrollPane(tblParkings), BorderLayout.CENTER);
        loadData();
        // Panel de Acciones
        JPanel panelActions = new JPanel(new FlowLayout());
        btnAdd = new JButton("Agregar Nuevo");
        btnUpdate = new JButton("Modificar");
        btnDelete = new JButton("Eliminar");
        btnBack = new JButton("Volver al Menú");

        panelActions.add(btnAdd);
        panelActions.add(btnUpdate);
        panelActions.add(btnDelete);
        panelActions.add(btnBack);
        add(panelActions, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            // PASO CLAVE: Pasar el objeto 'currentUser' que recibió esta ventana
            new ParkingCreateFrame(currentUser).setVisible(true);
            this.dispose();
        });

        // Dentro del constructor de ParkingManagementFrame
        btnBack.addActionListener(e -> {
            // 1. Verificamos que el usuario no sea nulo para evitar errores al reabrir el Dashboard
            if (currentUser != null) {
                // 2. Creamos la nueva instancia del Dashboard pasando el usuario actual
                new Dashboard(currentUser).setVisible(true);
                // 3. Cerramos SOLO esta ventana, no el programa completo
                this.dispose();
            } else {
                // Plan B: Si por alguna razón se perdió la sesión, enviamos al Login
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });

       btnUpdate.addActionListener(e -> {
    int selectedRow = tblParkings.getSelectedRow();
    if (selectedRow != -1) {
        // Obtenemos el nombre desde la columna 1 de la tabla
        String name = tblParkings.getValueAt(selectedRow, 1).toString();
        try {
            // 1. Buscamos el objeto. Asegúrate de que el nombre de la variable coincida.
            model.entities.ParkingLot pToEdit = FileManager.getParkingByName(name);
            
            // 2. Abrimos la ventana pasando el usuario y el objeto a editar
            // Usamos 'pToEdit' en ambos lados para evitar el "cannot find symbol"
            new ParkingCreateFrame(currentUser, pToEdit).setVisible(true);
            this.dispose();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar parqueo: " + ex.getMessage());
        }
    } else {
        JOptionPane.showMessageDialog(this, "Selecciona una sede para re-configurar.");
    }
});

        btnDelete.addActionListener(e -> {
            int selectedRow = tblParkings.getSelectedRow();

            if (selectedRow != -1) {
                // Extraemos el nombre (Columna 1) para identificarlo
                String nameToDelete = tblParkings.getValueAt(selectedRow, 1).toString();

                int confirm = JOptionPane.showConfirmDialog(this,
                        "¿Estás seguro de eliminar el parqueo: " + nameToDelete + "?",
                        "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        // 1. Borramos del archivo
                        FileManager.deleteParking(nameToDelete);
                        // 2. Refrescamos la tabla para ver el cambio
                        loadData();
                        JOptionPane.showMessageDialog(this, "Parqueo eliminado con éxito.");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage());
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona un parqueo de la tabla.");
            }
        });
    }

    private void loadData() {
        DefaultTableModel model = (DefaultTableModel) tblParkings.getModel();
        model.setRowCount(0);

        try {
            List<String> lines = FileManager.readAllLines("parkings.txt");
            int idTable = 1;

            for (String line : lines) {
                String[] data = line.split("\\|");

                // Verificamos que tengamos al menos 3 campos (Nombre, Total, Pref)
                if (data.length >= 3) {
                    model.addRow(new Object[]{
                        idTable++, // ID generado por la vista
                        data[0], // Nombre del parqueo
                        data[1], // Capacidad Total
                        data[2] // Espacios Preferenciales
                    });
                }
            }
        } catch (IOException e) {
            // La vista informa al usuario final
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Error loading parking data: " + e.getMessage(),
                    "Database Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
}
