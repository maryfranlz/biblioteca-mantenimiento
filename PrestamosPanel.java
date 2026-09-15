import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class PrestamosPanel extends JPanel{
    private DefaultTableModel modelo;
    private Biblioteca biblioteca;
    private Runnable onUpdate;
    
    public PrestamosPanel(Biblioteca biblioteca){
        this(biblioteca, null);
    }

    public PrestamosPanel(Biblioteca biblioteca, Runnable onUpdate){
        this.biblioteca = biblioteca;
        this.onUpdate = onUpdate;
        
        setLayout(new BorderLayout());
        setBackground(Recursos.COLOR_FONDO);
        setBorder(new EmptyBorder(24, 22, 25, 22));

        JPanel pnlSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        pnlSuperior.setOpaque(false);

        JButton btnExtenderPrestamo = Recursos.crearBoton("Extender préstamo", null);
        btnExtenderPrestamo.setBackground(new Color(40, 167, 69));
        btnExtenderPrestamo.setForeground(Color.WHITE);
        btnExtenderPrestamo.addActionListener(e -> {
            boolean hayElegibles = biblioteca.getPrestamos().stream()
                .anyMatch(p -> p.getEstado() == Prestamo.ACTIVO && !p.isExtensionUtilizada());
            if (!hayElegibles) {
                JOptionPane.showMessageDialog(this, "No hay préstamos activos elegibles para extensión.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new ExtensionDialog(SwingUtilities.getWindowAncestor(this), biblioteca, this::refrescarTodo).setVisible(true);
        });

        JButton btnDevolver = Recursos.crearBoton("Realizar devolución", null);
        btnDevolver.setBackground(Recursos.COLOR_NARANJA);
        btnDevolver.setForeground(Color.WHITE);
        btnDevolver.addActionListener(e -> {
            boolean hayActivos = biblioteca.getPrestamos().stream().anyMatch(Prestamo::estaActivo);
            if (!hayActivos) {
                JOptionPane.showMessageDialog(this, "No hay préstamos activos para devolver.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new DevolucionDialog(SwingUtilities.getWindowAncestor(this), biblioteca, this::refrescarTodo).setVisible(true);
        });

        JButton btnPrestar = Recursos.crearBoton("Realizar préstamo", null);
        btnPrestar.setBackground(Recursos.COLOR_ROJO_OSCURO);
        btnPrestar.setForeground(Color.WHITE);
        btnPrestar.addActionListener(e -> {
            if (biblioteca.getLibros(false).isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay libros disponibles para prestar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (biblioteca.getUsuarios().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay usuarios registrados en el sistema.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new PrestamoDialog(SwingUtilities.getWindowAncestor(this), biblioteca, this::refrescarTodo).setVisible(true);
        });

        pnlSuperior.add(btnPrestar);
        pnlSuperior.add(btnDevolver);
        pnlSuperior.add(btnExtenderPrestamo);
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

    public void refrescarTodo() {
        refresh();
        if (onUpdate != null) {
            onUpdate.run();
        }
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
