package view;

import model.data.FileManager;
import model.entities.User;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ReportSelectionDialog extends JDialog {
    private boolean confirmed = false;
    private String selectedSede;
    private JComboBox<String> sedeCombo;
    
    // Colores característicos de Heap Haven
    private final Color COLOR_FONDO = new Color(30, 30, 30);
    private final Color COLOR_CELESTE = new Color(33, 150, 243);
    private final Color COLOR_TEXTO = Color.WHITE;

    public ReportSelectionDialog(Frame parent, User user, FileManager fileManager) {
        super(parent, "GENERAR REPORTE", true);
        setUndecorated(true); // Para que luzca como tus otros frames sin barra de Windows
        setSize(450, 250);
        setLocationRelativeTo(parent);
        
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(COLOR_FONDO);
        contentPane.setBorder(BorderFactory.createLineBorder(COLOR_CELESTE, 1));
        setContentPane(contentPane);

        // --- ENCABEZADO ESTILO HEAP HAVEN ---
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(45, 45, 45));
        JLabel title = new JLabel(" CONFIGURACIÓN DEL REPORTE");
        title.setForeground(COLOR_CELESTE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.add(title);
        contentPane.add(header, BorderLayout.NORTH);

        // --- CUERPO CENTRAL ---
        JPanel body = new JPanel(null);
        body.setOpaque(false);
        
        JLabel lblSede = new JLabel("SELECCIONAR ÁREA DE COBERTURA:");
        lblSede.setForeground(new Color(180, 180, 180));
        lblSede.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSede.setBounds(40, 40, 300, 20);
        body.add(lblSede);

        sedeCombo = new JComboBox<>();
        sedeCombo.setBackground(new Color(50, 50, 50));
        sedeCombo.setForeground(Color.WHITE);
        sedeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sedeCombo.setBounds(40, 70, 370, 35);
        sedeCombo.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
        
        // Carga de datos
        if (user.getAssignedParking().equalsIgnoreCase("TODOS")) {
            sedeCombo.addItem("TODOS (REPORTE GLOBAL)");
            try {
                List<String> sedes = fileManager.readAllParkingLines().stream()
                        .map(line -> line.split("\\|")[0])
                        .collect(Collectors.toList());
                for (String s : sedes) sedeCombo.addItem(s);
            } catch (Exception e) { sedeCombo.addItem("ERROR AL CARGAR"); }
        } else {
            sedeCombo.addItem(user.getAssignedParking());
        }
        body.add(sedeCombo);
        contentPane.add(body, BorderLayout.CENTER);

        // --- BOTONERA ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footer.setOpaque(false);

        JButton btnCancel = createStyledButton("CANCELAR", new Color(70, 70, 70));
        JButton btnConfirm = createStyledButton("GENERAR PDF", COLOR_CELESTE);

        btnCancel.addActionListener(e -> dispose());
        btnConfirm.addActionListener(e -> {
            confirmed = true;
            // Limpiamos el texto si elegimos la opción global
            selectedSede = sedeCombo.getSelectedItem().toString().replace(" (REPORTE GLOBAL)", "");
            dispose();
        });

        footer.add(btnCancel);
        footer.add(btnConfirm);
        contentPane.add(footer, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public boolean isConfirmed() { return confirmed; }
    public String getSelectedSede() { return selectedSede; }
}