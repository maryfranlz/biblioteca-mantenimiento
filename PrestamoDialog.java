import java.awt.*;
import java.util.List;
import javax.swing.*;

public class PrestamoDialog extends JDialog{
    
    public PrestamoDialog(Window owner, Biblioteca biblioteca, Runnable onSave) {
        this(owner, biblioteca, null, onSave);
    }

    public PrestamoDialog(Window owner, Biblioteca biblioteca, Libro libroPreseleccionado, Runnable onSave) {
        super(owner, "Prestar libro", ModalityType.APPLICATION_MODAL);
        setSize(440, 330);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        List<Usuario> usuarios = biblioteca.getUsuarios();
        JComboBox<Usuario> cbUsuarios = new JComboBox<>(usuarios.toArray(new Usuario[0]));
        JComboBox<Libro> cbLibros = new JComboBox<>();

        if(libroPreseleccionado != null){
            cbLibros.addItem(libroPreseleccionado);
            cbLibros.setEnabled(false);
        } else {
            List<Libro> librosDisponibles = biblioteca.getLibros(false);
            for(Libro l : librosDisponibles) {
                cbLibros.addItem(l);
            }
        }

        JPanel campos = new JPanel(new GridLayout(0, 2, 8, 8));
        campos.add(new JLabel("Usuario"));
        campos.add(cbUsuarios);
        campos.add(new JLabel("Libro"));
        campos.add(cbLibros);

        JButton btnCancel = new JButton("Cancelar");
        JButton btnGuardar = new JButton("Prestar libro");
        btnCancel.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> {
            Usuario u = (Usuario) cbUsuarios.getSelectedItem();
            Libro l = (Libro) cbLibros.getSelectedItem();

            if(u == null || l == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un usuario y un libro válidos");
                return;
            }

            if(biblioteca.prestarLibro(l.getIsbn(), u.getId())){
                onSave.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo realizar el préstamo.");
            }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnCancel);
        botones.add(btnGuardar);
        
        panel.add(campos, BorderLayout.NORTH);
        panel.add(botones, BorderLayout.SOUTH);
        add(panel);
    }
}
