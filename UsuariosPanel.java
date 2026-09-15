import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class UsuariosPanel extends JPanel{
    private Biblioteca biblioteca;
    private Runnable onUpdate;
    private DefaultTableModel modelo;
    private JTable tablaUsuarios;

    public UsuariosPanel(Biblioteca biblioteca){
        this(biblioteca, null);
    }

    public UsuariosPanel(Biblioteca biblioteca, Runnable onUpdate){
        this.biblioteca = biblioteca;
        this.onUpdate = onUpdate;

        setLayout(new BorderLayout(0, 14));
        setBackground(Recursos.COLOR_FONDO);
        setBorder(new EmptyBorder(24,22,24,22));

        JPanel pnlSuperior = new JPanel(new BorderLayout(10, 0));
        pnlSuperior.setOpaque(false);

        //Botón nuevo usuario
        JButton btnNuevoUsuario = Recursos.crearBoton("Nuevo usuario", "plus.png");
        btnNuevoUsuario.setBackground(Recursos.COLOR_ROJO_OSCURO);
        btnNuevoUsuario.setForeground(Color.WHITE);
        btnNuevoUsuario.addActionListener(e -> new UsuarioDialog(
            SwingUtilities.getWindowAncestor(this), biblioteca, null, () -> {
                refresh();
                if(onUpdate != null) onUpdate.run();
            }
        ).setVisible(true));

        //Estructura panel superior
        pnlSuperior.add(btnNuevoUsuario, BorderLayout.EAST);
        add(pnlSuperior, BorderLayout.NORTH);

        JPanel pnlContenido = Recursos.panelBlanco(new BorderLayout());
        JLabel lblUsuarios = new JLabel("Usuarios");
        lblUsuarios.setFont(new Font("Arial", Font.BOLD, 12));
        pnlContenido.add(lblUsuarios, BorderLayout.NORTH);

        //Tabla usuarios
        String[] columnas = {"ID", "Nombre", "Libros prestados", "Editar", "Eliminar"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override 
            public boolean isCellEditable(int r, int c) {
                return c >= 3;
            }
        };
        tablaUsuarios = new JTable(modelo);
        Recursos.estiloTablas(tablaUsuarios);

        for(int c = 3; c < columnas.length; c++) {
            tablaUsuarios.getColumnModel().getColumn(c).setCellRenderer(new RenderBoton());
            tablaUsuarios.getColumnModel().getColumn(c).setCellEditor(new EditorBoton(new JCheckBox(), this::action));
        }

        //Estructura panel contenido
        pnlContenido.add(new JScrollPane(tablaUsuarios), BorderLayout.CENTER);
        add(pnlContenido, BorderLayout.CENTER);
        refresh();
    }

    public void refresh() {
        modelo.setRowCount(0);
        List<Usuario> usuarios = biblioteca.getUsuarios();
        for(Usuario usuario : usuarios) {
            modelo.addRow(new Object[] {
                usuario.getId(),
                usuario.getNombre(),
                usuario.getLibrosPrestados().size() + " libro(s)",
                "Editar",
                "Eliminar"
            });
        }
    }

    private void action(int fila, int columna) {
        String idUsuario = String.valueOf(modelo.getValueAt(fila, 0));
        Usuario usuario = biblioteca.buscarUsuarioPorId(idUsuario);

        if (usuario == null) {
            return;
        }

        switch (columna) {
            case 3 -> new UsuarioDialog(SwingUtilities.getWindowAncestor(this), biblioteca, usuario, () -> {
                refresh();
                if(onUpdate != null) onUpdate.run();
            }).setVisible(true);
            case 4 -> {
                if(!usuario.getLibrosPrestados().isEmpty()){
                    JOptionPane.showMessageDialog(this,
                        "No se puede eliminar al usuario \"" + usuario.getNombre() + "\" porque tiene " + usuario.getLibrosPrestados().size() + " libro(s) prestado(s).",
                        "Operación denegada",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
                int r = JOptionPane.showConfirmDialog(this, 
                    "¿Deseas eliminar al usuario \"" + usuario.getNombre() + "\"?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION
                );
                if (r == JOptionPane.YES_OPTION) {
                    if(biblioteca.eliminarUsuario(idUsuario)){
                        refresh();
                        if(onUpdate != null) onUpdate.run();
                    } else {
                        JOptionPane.showMessageDialog(this, "No se pudo eliminar al usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
}