import java.awt.*;
import javax.swing.*;
import java.util.List;

public class ExtensionDialog extends JDialog{
    
    public ExtensionDialog(Window owner, Biblioteca biblioteca, Runnable onSave){
        this(owner, biblioteca, null, onSave);
    }

    public ExtensionDialog(Window owner, Biblioteca biblioteca, Libro libroPreseleccionado, Runnable onSave) {
        super(owner, "Extender préstamo", ModalityType.APPLICATION_MODAL);
        setSize(440, 300);
        setLocationRelativeTo(owner);

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JComboBox<Prestamo> cbPrestamos = new JComboBox<>();

        if(libroPreseleccionado != null) {
            Prestamo p = biblioteca.buscarPrestamoActivo(libroPreseleccionado);

            if(p != null && p.getEstado() == Prestamo.ACTIVO && !p.isExtensionUtilizada()){
                cbPrestamos.addItem(p);
                cbPrestamos.setEnabled(false);
            }
        } else {
            List<Prestamo> elegibles = biblioteca.getPrestamos().stream()
                .filter(p -> p.getEstado() == Prestamo.ACTIVO && !p.isExtensionUtilizada()).toList();
            
            for(Prestamo p : elegibles){
                cbPrestamos.addItem(p);
            }
        }

        JPanel campos = new JPanel(new GridLayout(0, 2, 8 ,8));
        campos.add(new JLabel("Préstamo:"));
        campos.add(cbPrestamos);
        
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnGuardar = new JButton("Extender préstamo");
        if(cbPrestamos.getItemCount() == 0){
            btnGuardar.setEnabled(false);
        }
        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> {
            Prestamo p = (Prestamo) cbPrestamos.getSelectedItem();
            if(p != null && biblioteca.extenderPrestamo(p.getLibro().getIsbn(), p.getUsuario().getId())){
                JOptionPane.showMessageDialog(this, "Préstamo extendido con éxito.");
                onSave.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo procesar la extensión.");
            }
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.add(btnCancelar);
        botones.add(btnGuardar);
        
        panel.add(campos, BorderLayout.NORTH);
        panel.add(botones, BorderLayout.SOUTH);
        add(panel);
    }
}
