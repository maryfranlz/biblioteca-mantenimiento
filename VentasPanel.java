import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class VentasPanel extends JPanel {
    private Biblioteca biblioteca;
    private DefaultTableModel modelo;
    private JTable tablaVentas;
    private JTextField txtBuscar = new JTextField();
    private JLabel lblTotalVendidosValor = new JLabel("0");
    private JLabel lblTotalIngresosValor = new JLabel("$0.00");

    public VentasPanel(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;

        setLayout(new BorderLayout(0, 14));
        setBackground(Recursos.COLOR_FONDO);
        setBorder(new EmptyBorder(24, 22, 24, 22));

        // Panel superior: Métricas y buscador
        JPanel pnlSuperior = new JPanel(new BorderLayout(14, 0));
        pnlSuperior.setOpaque(false);

        // Tarjetas de resumen
        JPanel pnlTarjetas = new JPanel(new GridLayout(1, 2, 12, 0));
        pnlTarjetas.setOpaque(false);
        pnlTarjetas.add(crearTarjeta("Libros Vendidos", lblTotalVendidosValor));
        pnlTarjetas.add(crearTarjeta("Total Recaudado en Ventas", lblTotalIngresosValor));

        // Buscador
        txtBuscar.setPreferredSize(new Dimension(280, 32));
        txtBuscar.setBorder(new EmptyBorder(0, 5, 0, 5));

        JLabel buscadorIcono = new JLabel();
        ImageIcon searchIcon = new ImageIcon("search-symbol.png");
        if (new java.io.File("search-symbol.png").exists()) {
            Image smallSearchIcon = searchIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            buscadorIcono.setIcon(new ImageIcon(smallSearchIcon));
        }
        buscadorIcono.setBorder(new EmptyBorder(2, 5, 2, 5));

        JPanel pnlBuscador = new JPanel(new BorderLayout());
        pnlBuscador.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        pnlBuscador.setBackground(Color.WHITE);
        pnlBuscador.add(buscadorIcono, BorderLayout.WEST);
        pnlBuscador.add(txtBuscar, BorderLayout.CENTER);

        JPanel pnlDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlDerecha.setOpaque(false);
        pnlDerecha.add(pnlBuscador);

        pnlSuperior.add(pnlTarjetas, BorderLayout.WEST);
        pnlSuperior.add(pnlDerecha, BorderLayout.EAST);
        add(pnlSuperior, BorderLayout.NORTH);

        // Panel central: Tabla de ventas
        JPanel pnlContenido = Recursos.panelBlanco(new BorderLayout(0, 10));
        JLabel lblTituloSeccion = new JLabel("Historial y Registro de Libros Vendidos");
        lblTituloSeccion.setFont(new Font("Arial", Font.BOLD, 13));
        pnlContenido.add(lblTituloSeccion, BorderLayout.NORTH);

        String[] columnas = {
            "ISBN", "Título", "Categoría", "Precio", "Comprador", "Identificación / DNI", "Teléfono", "Correo", "Fecha"
        };
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaVentas = new JTable(modelo);
        Recursos.estiloTablas(tablaVentas);
        pnlContenido.add(new JScrollPane(tablaVentas), BorderLayout.CENTER);
        add(pnlContenido, BorderLayout.CENTER);

        txtBuscar.getDocument().addDocumentListener((SimpleDocumentListener) e -> refresh());
        refresh();
    }

    private JPanel crearTarjeta(String titulo, JLabel lblValor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Recursos.COLOR_NARANJA);
        card.setBorder(new EmptyBorder(10, 14, 10, 14));
        card.setPreferredSize(new Dimension(210, 65));

        JLabel t = new JLabel(titulo);
        t.setFont(new Font("Arial", Font.PLAIN, 12));
        t.setForeground(Color.WHITE);

        lblValor.setFont(new Font("Arial", Font.BOLD, 18));
        lblValor.setForeground(Color.WHITE);

        card.add(t);
        card.add(Box.createVerticalStrut(4));
        card.add(lblValor);
        return card;
    }

    public void refresh() {
        modelo.setRowCount(0);

        String filtro = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();
        List<Venta> listaVentas = biblioteca.getVentas();

        double totalIngresos = 0.0;
        int librosVendidosCount = 0;

        for (Venta v : listaVentas) {
            totalIngresos += v.getPrecio();
            librosVendidosCount++;

            boolean coincide = filtro.isEmpty() ||
                (v.getLibro() != null && v.getLibro().getTitulo().toLowerCase().contains(filtro)) ||
                (v.getLibro() != null && v.getLibro().getIsbn().toLowerCase().contains(filtro)) ||
                (v.getNombreComprador() != null && v.getNombreComprador().toLowerCase().contains(filtro)) ||
                (v.getIdentificacion() != null && v.getIdentificacion().toLowerCase().contains(filtro));

            if (coincide) {
                modelo.addRow(new Object[]{
                    v.getLibro() != null ? v.getLibro().getIsbn() : "N/A",
                    v.getLibro() != null ? v.getLibro().getTitulo() : "N/A",
                    (v.getLibro() != null && v.getLibro().getCategoria() != null) ? v.getLibro().getCategoria().name() : "N/A",
                    String.format("$%.2f", v.getPrecio()),
                    v.getNombreComprador(),
                    v.getIdentificacion(),
                    v.getTelefono(),
                    v.getCorreo(),
                    v.getFecha() != null ? v.getFecha().toString() : "-"
                });
            }
        }

        lblTotalVendidosValor.setText(String.valueOf(librosVendidosCount));
        lblTotalIngresosValor.setText(String.format("$%.2f", totalIngresos));
    }
}
