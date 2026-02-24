package view;

import controller.ParkingController;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ParkingConfigView extends JDialog {

    private final JPanel spacesGridPanel;
    private final ParkingController controller;
    private final JLabel lblSedeSelected;
    private static final Color COLOR_FONDO = new Color(18, 18, 18);
    private static final Color COLOR_CELESTE = new Color(33, 150, 243);
    private static final Color COLOR_ACCENTO = new Color(30, 30, 30);

    public ParkingConfigView(JFrame parent, List<String[]> summaryData, ParkingController controller, String initialSede) {
        super(parent, true);
        this.controller = controller;
        this.spacesGridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        this.lblSedeSelected = new JLabel("SEDE: " + (initialSede != null ? initialSede.toUpperCase() : "---"));

        setSize(950, 650);
        setLocationRelativeTo(parent);
        setUndecorated(true);

        setupInterface();

        if (initialSede != null) {
            loadSpaceDetails(initialSede);
        }
    }

    private void setupInterface() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_FONDO);
        root.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 2));

        // Barra de Título
        root.add(createTitleBar("VISOR TÉCNICO DE ESTRUCTURA - Heap Haven"), BorderLayout.NORTH);

        // Panel de Contenido Directo 
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Header
        JPanel detailHeader = new JPanel(new BorderLayout());
        detailHeader.setOpaque(false);
        detailHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(80, 80, 80)));

        lblSedeSelected.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblSedeSelected.setForeground(COLOR_CELESTE);
        detailHeader.add(lblSedeSelected, BorderLayout.WEST);

        // Grid de espacios
        spacesGridPanel.setBackground(COLOR_FONDO);
        JScrollPane scroll = new JScrollPane(spacesGridPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        // Footer con el botón inteligente unificado
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        JButton btnBack = new JButton("VOLVER");
        styleButton(btnBack);
        btnBack.addActionListener(e -> dispose());
        footer.add(btnBack);

        mainContent.add(detailHeader, BorderLayout.NORTH);
        mainContent.add(scroll, BorderLayout.CENTER);
        mainContent.add(footer, BorderLayout.SOUTH);

        root.add(mainContent, BorderLayout.CENTER);
        add(root);
    }

    public void loadSpaceDetails(String sedeName) {
        try {
            List<String[]> details = controller.getDetailedSpaceConfig(sedeName);
            renderSpaceDetail(details);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void renderSpaceDetail(List<String[]> details) {
        spacesGridPanel.removeAll();
        for (String[] space : details) {
            JPanel card = new JPanel(new BorderLayout(15, 5));
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 70, 70), 1),
                    BorderFactory.createEmptyBorder(12, 15, 12, 15)
            ));
            card.setBackground(COLOR_ACCENTO);

            // Letra/Icono del tipo
            JLabel lblIcon = new JLabel(space[1].substring(0, 1).toUpperCase());
            lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 28));
            lblIcon.setForeground(COLOR_CELESTE);

            JLabel lblInfo = new JLabel("<html><b>" + space[0].toUpperCase() + "</b><br><font color='#AAAAAA'>" + space[1] + "</font></html>");
            lblInfo.setForeground(Color.WHITE);
            lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            JLabel lblType = new JLabel(space[2]);
            lblType.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblType.setForeground(space[2].contains("PREF") ? new Color(100, 200, 255) : new Color(120, 120, 120));

            card.add(lblIcon, BorderLayout.WEST);
            card.add(lblInfo, BorderLayout.CENTER);
            card.add(lblType, BorderLayout.SOUTH);

            spacesGridPanel.add(card);
        }
        spacesGridPanel.revalidate();
        spacesGridPanel.repaint();
    }

    private JPanel createTitleBar(String titleStr) {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(10, 10, 10));
        bar.setPreferredSize(new Dimension(getWidth(), 35));

        JLabel lblTitle = new JLabel("   " + titleStr);
        lblTitle.setForeground(new Color(150, 150, 150));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));

        bar.add(lblTitle, BorderLayout.WEST);
        return bar;
    }

    private void styleButton(JButton b) {
        b.setPreferredSize(new Dimension(150, 35));
        b.setBackground(new Color(50, 50, 50));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(COLOR_CELESTE));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
