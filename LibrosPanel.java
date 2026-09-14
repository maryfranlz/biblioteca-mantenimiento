import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class LibrosPanel extends JPanel{
    private Biblioteca biblioteca;
    private DefaultTableModel modelo;
    private JTable tabla;

    public LibrosPanel(Biblioteca biblioteca) {
        this.biblioteca = biblioteca;

        setLayout(new BorderLayout(0,14));
        setBackground(Recursos.COLOR_FONDO);
        setBorder(new EmptyBorder(25, 22, 25, 22));

        JPanel pnlSuperior = new JPanel();

        JButton btnNuevoLibro = Recursos.crearBoton("Nuevo libro", "plus.png");

        JTextField txtBuscar = new JTextField();

        JPanel contenido = new JPanel();

        String[] columnas = {"ISBN", "Título", "Autor", "Categoría", "Estado", "Devolver", "Prestar", "Extender", "Editar", "Eliminar", "Vender"};

        modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) {
                return c>= 5;
            }
        };
        tabla = new JTable(modelo);

        //TODO: terminar paneles individuales + pop ups
    }
}
