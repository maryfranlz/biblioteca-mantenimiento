import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class PrestamosPanel extends JPanel{
    private DefaultTableModel modelo;
    private Biblioteca biblioteca;
    
    public PrestamosPanel(Biblioteca biblioteca){
        this.biblioteca = biblioteca;
        
        setLayout(new BorderLayout());
        setBackground(Recursos.COLOR_FONDO);
        setBorder(new EmptyBorder(24, 22, 25, 22));

        JPanel pnlSuperior = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlSuperior.setOpaque(false);

        JPanel pnlBotones = new JPanel(new GridLayout(1, 3, 10, 0));
        pnlBotones.setOpaque(false);

        JButton btnExtenderPrestamo = Recursos.crearBoton("Extender préstamo", null);
        btnExtenderPrestamo.setBackground(Color.GREEN);
        btnExtenderPrestamo.setForeground(Color.WHITE);
        btnExtenderPrestamo.addActionListener(e -> 
            new ExtensionDialog(SwingUtilities.getWindowAncestor(this), biblioteca, this::refresh)
        .setVisible(true));

        JButton btnDevolver = Recursos.crearBoton("Realizar devolución", null);
        btnDevolver.setBackground(Recursos.COLOR_NARANJA);
        btnDevolver.setForeground(Color.WHITE);
        btnDevolver.addActionListener(e -> 
            new DevolucionDialog(SwingUtilities.getWindowAncestor(this), biblioteca, this::refresh)
        .setVisible(true));

        JButton btnPrestar = Recursos.crearBoton("Realizar préstamo", null);
        btnPrestar.setBackground(Recursos.COLOR_ROJO_OSCURO);
        btnPrestar.setForeground(Color.WHITE);
        btnPrestar.addActionListener(e -> 
            new PrestamoDialog(SwingUtilities.getWindowAncestor(this), biblioteca, this::refresh)
        .setVisible(true));

        Dimension tamanoBoton = new Dimension(125, 34);

        btnExtenderPrestamo.setPreferredSize(tamanoBoton);
        btnExtenderPrestamo.setMaximumSize(tamanoBoton);

        btnDevolver.setPreferredSize(tamanoBoton);
        btnDevolver.setMaximumSize(tamanoBoton);

        btnPrestar.setPreferredSize(tamanoBoton);
        btnPrestar.setMaximumSize(tamanoBoton);

        pnlBotones.add(btnExtenderPrestamo);
        pnlBotones.add(btnDevolver);
        pnlBotones.add(btnPrestar);

        JPanel pnlVacio = new JPanel();
        pnlVacio.setOpaque(false);

        pnlSuperior.add(pnlBotones);
        pnlSuperior.add(pnlVacio);

        pnlSuperior.setPreferredSize(new Dimension(0, 45));

        add(pnlSuperior, BorderLayout.NORTH);

        JPanel pnlContenido = Recursos.panelBlanco(new BorderLayout());
        JLabel lblHistorial = new JLabel("Historial de préstamos");
        lblHistorial.setFont(new Font("Arial", Font.BOLD, 12));
        pnlContenido.add(lblHistorial, BorderLayout.NORTH);

        String[] columnas = {"Usuario", "Libro", "Fecha préstamo", "Fecha límite", "Fecha devolución", "Estado", "Recargo"};
        modelo = new DefaultTableModel(columnas, 0){
            @Override 
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };

        JTable tablaPrestamos = new JTable(modelo);
        Recursos.estiloTablas(tablaPrestamos);
        pnlContenido.add(new JScrollPane(tablaPrestamos), BorderLayout.CENTER);
        add(pnlContenido);
        refresh();
    }

    public void refresh() {
        modelo.setRowCount(0);

        List<Prestamo> prestamos = biblioteca.getPrestamos();
        for (Prestamo p : prestamos) {
            modelo.addRow(new Object[]{
                p.getUsuario().getNombre(),
                p.getLibro().getTitulo(),
                p.getFechaPrestamo(),
                p.getFechaLimiteDevolucion(),
                p.getFechaDevolucion() == null ? "-" : p.getFechaDevolucion().toString(),
                p.getEstadoTexto(),
                p.getRecargo() == null ? "" : String.format("$%.2f", p.getRecargo())
            });
        }
    }
}
