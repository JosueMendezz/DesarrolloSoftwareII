package view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public abstract class BaseFrame extends JFrame {

    private Point initialClick;
    protected final Color COLOR_FONDO = new Color(25, 25, 25);
    protected final Color COLOR_CELESTE = new Color(33, 150, 243);
    protected final Color COLOR_ACCENTO = new Color(45, 45, 45);
    protected final Color COLOR_TEXTO = new Color(240, 240, 240);
    protected final Color COLOR_BARRA_TITULO = new Color(15, 15, 15);

    public BaseFrame(String title, int width, int height) {
        setTitle(title);
        setSize(width, height);
        setUndecorated(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(COLOR_FONDO);
    }

    // --- MÉTODOS DE ESTILO REUTILIZABLES ---
    protected JButton createStyledButton(String text, boolean highlighted) {
        JButton btn = new JButton(text.toUpperCase());
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (highlighted) {
            btn.setBackground(COLOR_CELESTE);
            btn.setForeground(Color.WHITE);
            btn.setBorder(null);
        } else {
            btn.setBackground(COLOR_ACCENTO);
            btn.setForeground(COLOR_TEXTO);
            btn.setBorder(BorderFactory.createLineBorder(COLOR_CELESTE, 1));
        }

        // Efecto Hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(highlighted ? COLOR_CELESTE.brighter() : COLOR_ACCENTO.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(highlighted ? COLOR_CELESTE : COLOR_ACCENTO);
            }
        });
        return btn;
    }

    protected void applyCustomTableStyle(JTable t) {
        t.setBackground(new Color(35, 35, 35));
        t.setForeground(Color.WHITE);
        t.setRowHeight(35);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setGridColor(new Color(60, 60, 60));
        t.setSelectionBackground(new Color(33, 150, 243, 80));
        t.setSelectionForeground(Color.WHITE);
        t.setShowVerticalLines(false);

        JTableHeader header = t.getTableHeader();
        header.setBackground(new Color(10, 10, 10));
        header.setForeground(COLOR_CELESTE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_CELESTE));

        if (t.getParent() instanceof JViewport) {
            JViewport viewport = (JViewport) t.getParent();
            viewport.setBackground(COLOR_FONDO);
            if (viewport.getParent() instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) viewport.getParent();
                scroll.setBorder(BorderFactory.createLineBorder(new Color(50, 50, 50)));
                scroll.getViewport().setBackground(COLOR_FONDO);
            }
        }
    }

    protected JLabel createHeaderLabel(String text) {
        JLabel lbl = new JLabel(text.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(COLOR_CELESTE);
        return lbl;
    }

    // --- LÓGICA DE BARRA DE TÍTULO ---
    protected void setupCustomTitleBar(String titleText) {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(COLOR_BARRA_TITULO);
        titleBar.setPreferredSize(new Dimension(getWidth(), 35));

        JLabel titleLabel = new JLabel("   " + titleText);
        titleLabel.setForeground(new Color(180, 180, 180));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleBar.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);

        // BOTÓN 1: MINIMIZAR
        JButton btnMinimize = createTitleBarButton("—");
        btnMinimize.addActionListener(e -> setState(JFrame.ICONIFIED));

        // BOTÓN 2: MAXIMIZAR / RESTAURAR (Corregido)
        JButton btnMaximize = createTitleBarButton("▢");
        btnMaximize.addActionListener(e -> {
            if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                setExtendedState(JFrame.NORMAL);
            } else {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });

        // La "X" eliminada 
        buttonPanel.add(btnMinimize);
        buttonPanel.add(btnMaximize);

        titleBar.add(buttonPanel, BorderLayout.EAST);
        add(titleBar, BorderLayout.NORTH);
        enableWindowDragging(titleBar);
    }

    private JButton createTitleBarButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(45, 35));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setContentAreaFilled(true);
                btn.setBackground(new Color(60, 60, 60));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setContentAreaFilled(false);
            }
        });
        return btn;
    }

    private void enableWindowDragging(JPanel panel) {
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent e) {
                initialClick = e.getPoint();
            }
        });
        panel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent e) {
                setLocation(getLocation().x + e.getX() - initialClick.x,
                        getLocation().y + e.getY() - initialClick.y);
            }
        });
    }
}
