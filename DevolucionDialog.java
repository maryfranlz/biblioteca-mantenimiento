import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DevolucionDialog extends JDialog{

    public DevolucionDialog(Window owner, Biblioteca biblioteca, Runnable onSave) {
        this(owner, biblioteca, null, onSave);
    }

    public DevolucionDialog(Window owner, Biblioteca biblioteca, Libro libroPreseleccionado, Runnable onSave) {
        super(owner, "Devolver libroPreseleccionado", ModalityType.APPLICATION_MODAL);
        setSize(440, 330);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JComboBox<Prestamo> cbPrestamos = new JComboBox<>();

        if(libroPreseleccionado != null){
            Prestamo p = biblioteca.buscarPrestamoActivo(libroPreseleccionado);
            if(p != null){
                cbPrestamos.addItem(p);
                cbPrestamos.setEnabled(false);
            }
            else {
                JOptionPane.showMessageDialog(owner, "El libro no tiene un préstamo activo.");
                dispose();
                return;
            }
        } else {
            List<Prestamo> prestamosActivos = biblioteca.getPrestamos().stream()
                .filter(Prestamo::estaActivo)
                 .toList();

            if(prestamosActivos.isEmpty()){
                JOptionPane.showMessageDialog(owner, "No hay préstamos activos para devolver.");
                dispose();
                return;
            }

            for(Prestamo p : prestamosActivos){
                cbPrestamos.addItem(p);
            }
        }

        JPanel campos = new JPanel(new GridLayout(1,2,8,8));
        campos.add(new JLabel("Préstamo activo:"));
        campos.add(cbPrestamos);

        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar = new JButton("Devolver libro");
        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> {
            Prestamo p = (Prestamo) cbPrestamos.getSelectedItem();
            if(p != null && biblioteca.devolverLibro(p.getLibro().getIsbn(), p.getUsuario().getId())){
                String recargo = String.format("$%.2f", p.getRecargo());
                JOptionPane.showMessageDialog(this, "Devolución realizada. Recargo: " + recargo);
                onSave.run();
                dispose();
            } else JOptionPane.showMessageDialog(this, "No se pudo realizar la devolución.");
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnCancelar);
        botones.add(btnGuardar);

        panel.add(campos, BorderLayout.NORTH);
        panel.add(botones, BorderLayout.SOUTH);
        add(panel);
    }
}
