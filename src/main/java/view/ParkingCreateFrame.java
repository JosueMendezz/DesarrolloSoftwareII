/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;
import controller.ParkingController;
import javax.swing.*;
import java.awt.*;
import model.entities.ParkingLot;
import model.entities.User;
/**
 *
 * @author Caleb Murillo
 */
public class ParkingCreateFrame extends JFrame {
    private JTextField txtName = new JTextField(20);
    private JSpinner spinTotalSpaces = new JSpinner(new SpinnerNumberModel(1, 1, 500, 1));
    private JSpinner spinPreferential = new JSpinner(new SpinnerNumberModel(0, 0, 500, 1));
    
    private JButton btnExit = new JButton("Salir");
    private JButton btnBack = new JButton("Atrás");
    private JButton btnNext = new JButton("Siguiente");

    private User currentUser;
    private ParkingLot parkingToEdit; // Nulo si es nuevo, cargado si es edición

    // CONSTRUCTOR 1: Para crear nuevo (Soluciona el error de la imagen 13)
    public ParkingCreateFrame(User user) {
        this.currentUser = user;
        initLayout();
        setTitle("J-Node - Crear Nuevo Parqueo");
    }

    // CONSTRUCTOR 2: Para editar uno existente
    public ParkingCreateFrame(User user, ParkingLot existingParking) {
        this(user); // Llama al constructor de arriba para no repetir código
        this.parkingToEdit = existingParking;
        
        // Cargamos los datos actuales en la interfaz
        txtName.setText(existingParking.getName());
        spinTotalSpaces.setValue(existingParking.getNumberOfSpaces());
        spinPreferential.setValue(existingParking.getPreferentialSpaces());
        
        setTitle("J-Node - Modificar Sede: " + existingParking.getName());
        btnNext.setText("Re-configurar Espacios");
    }

    private void initLayout() {
        setSize(400, 300);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panelForm = new JPanel(new GridLayout(3, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panelForm.add(new JLabel("Nombre del Parqueo:"));
        panelForm.add(txtName);
        panelForm.add(new JLabel("Total de Espacios:"));
        panelForm.add(spinTotalSpaces);
        panelForm.add(new JLabel("Espacios Discapacidad:"));
        panelForm.add(spinPreferential);

        add(panelForm, BorderLayout.CENTER);

        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButtons.add(btnExit);
        panelButtons.add(btnBack);
        panelButtons.add(btnNext);
        add(panelButtons, BorderLayout.SOUTH);

        // LÓGICA DE NAVEGACIÓN
   btnNext.addActionListener(e -> {
    try {
        String name = txtName.getText().trim();
        int total = (int) spinTotalSpaces.getValue();
        int pref = (int) spinPreferential.getValue();

        // 1. VALIDACIÓN DE NOMBRE DUPLICADO
        // Solo validamos si es un parqueo NUEVO
        if (parkingToEdit == null) {
            if (model.data.FileManager.exists(name)) {
                throw new Exception("Error: El nombre del parqueo ya existe. Intente con otro nombre.");
            }
        } 
        // Si estamos editando y cambiamos el nombre a uno que ya usa OTRA sede
        else if (!name.equalsIgnoreCase(parkingToEdit.getName())) {
            if (model.data.FileManager.exists(name)) {
                throw new Exception("Error: No puedes renombrarlo así, ese nombre ya pertenece a otra sede.");
            }
        }

        // 2. CONTINUAR CON LA LÓGICA NORMAL
        if (pref > total) throw new Exception("La cuota preferencial excede el total.");

        ParkingController controller = new ParkingController();
        String originalName = name;

        if (parkingToEdit != null) {
            originalName = parkingToEdit.getName(); 
            parkingToEdit.setName(name);
            parkingToEdit.setNumberOfSpaces(total);
            parkingToEdit.setPreferentialSpaces(pref);
            controller.prepareForEditing(parkingToEdit);
        } else {
            controller.prepareParking(name, total, pref);
        }
        
        new SpaceConfigFrame(controller, currentUser, originalName).setVisible(true);
        this.dispose();
        
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
    }
});
        btnBack.addActionListener(e -> {
            new ParkingManagementFrame(currentUser).setVisible(true);
            this.dispose();
        });

        btnExit.addActionListener(e -> {
            new Dashboard(currentUser).setVisible(true);
            this.dispose();
        });
    }
}


