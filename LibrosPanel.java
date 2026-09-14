import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class LibrosPanel extends JPanel{
    private Biblioteca biblioteca;
    private InicioPanel pnlInicio;
    private DefaultTableModel modelo;
    private JTable tablaLibros;
    private JTextField txtBuscar = new JTextField();
    private JComboBox<String> categoria = new JComboBox<>();

    public LibrosPanel(Biblioteca biblioteca, InicioPanel pnlInicio) {
        this.biblioteca = biblioteca;
        this.pnlInicio = pnlInicio;

        setLayout(new BorderLayout(0,14));
        setBackground(Recursos.COLOR_FONDO);
        setBorder(new EmptyBorder(24, 22, 24, 22));

        JPanel pnlSuperior = new JPanel(new BorderLayout(10, 0));
        pnlSuperior.setOpaque(false);        

        //Buscador
        txtBuscar.setPreferredSize(new Dimension(280, 32));
        txtBuscar.setBorder(new EmptyBorder(0,5,0,5));

        JLabel buscadorIcono = new JLabel();
        ImageIcon searchIcon = new ImageIcon("search-symbol.png");
        Image smallSearchIcon = searchIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        ImageIcon lupita = new ImageIcon(smallSearchIcon);
        buscadorIcono.setIcon(lupita);
        buscadorIcono.setBorder(new EmptyBorder(2,5,2,5));

        JPanel pnlBuscador = new JPanel(new BorderLayout());
        pnlBuscador.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        pnlBuscador.setBackground(Color.WHITE);
        pnlBuscador.add(buscadorIcono, BorderLayout.WEST);
        pnlBuscador.add(txtBuscar, BorderLayout.CENTER);

        //Filtro categorías
        categoria.addItem("Todas las categorías");
        for(CategoriaLibro c : CategoriaLibro.values()){
            categoria.addItem(c.name());
        }

        //Botón nuevo libro
        JButton btnNuevoLibro = Recursos.crearBoton("Nuevo libro", "plus.png");
        btnNuevoLibro.setBackground(Recursos.COLOR_ROJO_OSCURO);
        btnNuevoLibro.setForeground(Color.WHITE);
        btnNuevoLibro.addActionListener(e -> new LibroDialog(
            SwingUtilities.getWindowAncestor(this), biblioteca, null, this::refresh
        ).setVisible(true));

        //Extructura panel superior
        pnlSuperior.add(pnlBuscador, BorderLayout.WEST);
        pnlSuperior.add(categoria, BorderLayout.CENTER);
        pnlSuperior.add(btnNuevoLibro, BorderLayout.EAST);
        add(pnlSuperior, BorderLayout.NORTH);

        JPanel pnlContenido = Recursos.panelBlanco(new BorderLayout());
        JLabel lblCatalogo = new JLabel("Catálogo de libros");
        lblCatalogo.setFont(new Font("Arial", Font.BOLD, 12));
        pnlContenido.add(lblCatalogo, BorderLayout.NORTH);

        //Tabla catálogo de libros
        String[] columnas = {"ISBN", "Título", "Autor", "Categoría", "Estado", "Devolver", "Prestar", "Extender", "Editar", "Eliminar", "Vender"};
        modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) {
                return c>= 5;
            }
        };
        tablaLibros = new JTable(modelo);
        Recursos.estiloTablas(tablaLibros);

        //Estructura panel contenido
        pnlContenido.add(new JScrollPane(tablaLibros), BorderLayout.CENTER);
        add(pnlContenido, BorderLayout.CENTER);

        txtBuscar.getDocument().addDocumentListener((SimpleDocumentListener) e -> refresh());
        categoria.addActionListener(e -> refresh());
        refresh();
    }

    private void refresh() {
        modelo.setRowCount(0);
        
        List<Libro> libros = biblioteca.buscarLibros(txtBuscar.getText());
        for(Libro libro : libros) {
            modelo.addRow(new Object[] {
                libro.getIsbn(), libro.getTitulo(), libro.getAutor(), libro.getCategoria(), libro.getEstadoTexto(), "Devolver", "Prestar", "Extender", "Editar", "Eliminar", "Vender"
            });
        }

        for(int c = 5; c < modelo.getColumnCount(); c++) {
            tablaLibros.getColumnModel().getColumn(c).setCellRenderer(new RenderBoton());
            tablaLibros.getColumnModel().getColumn(c).setCellEditor(new EditorBoton(new JCheckBox(), this::action));
        }
    }

    private void action(int fila, int columna) {
        String isbn = String.valueOf(modelo.getValueAt(fila, 0));
        Libro libro = biblioteca.buscarLibroPorIsbn(isbn);
        if(libro == null){
            return;
        }

        switch (columna) {
            case 5 -> new DevolucionDialog(SwingUtilities.getWindowAncestor(this), biblioteca, libro, this::refresh).setVisible(true);
            case 6 -> new PrestamoDialog(SwingUtilities.getWindowAncestor(this), biblioteca, libro, this::refresh).setVisible(true);
            case 7 -> new ExtensionDialog(SwingUtilities.getWindowAncestor(this), biblioteca, libro, this::refresh).setVisible(true);
            case 8 -> new LibroDialog(SwingUtilities.getWindowAncestor(this), biblioteca, libro, this::refresh).setVisible(true);
            case 9 -> {
                int r = JOptionPane.showConfirmDialog(this, "¿Eliminar el libro seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if(r == JOptionPane.YES_OPTION && biblioteca.eliminarLibro(isbn)){
                    refresh();
                }
            }
            case 10 -> {
                int r = JOptionPane.showConfirmDialog(this,
                    "Vender un ejemplar de \"" + libro.getTitulo() + "\" por $" + libro.getPrecio() + "?",
                    "Confirmar venta",
                    JOptionPane.YES_NO_OPTION
                );
                if(r == JOptionPane.YES_OPTION && biblioteca.venderLibro(isbn)){
                    refresh();
                    pnlInicio.actualizar();
                }
            }
        }

    }
}
